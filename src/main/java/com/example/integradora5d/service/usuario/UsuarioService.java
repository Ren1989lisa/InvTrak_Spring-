package com.example.integradora5d.service.usuario;

import com.example.integradora5d.dto.auth.ResetPasswordDTO;
import com.example.integradora5d.dto.usuario.CreateUsuarioDto;
import com.example.integradora5d.dto.usuario.UpdateUsuarioDTO;
import com.example.integradora5d.dto.usuario.UsuarioForClientDTO;
import com.example.integradora5d.error.errorTypes.CustomBadRequestException;
import com.example.integradora5d.error.errorTypes.CustomNotContentException;
import com.example.integradora5d.error.errorTypes.CustomNotFoundException;
import com.example.integradora5d.mappers.UsuarioMapper;
import com.example.integradora5d.models.password_reset_token.BeanPasswordResetToken;
import com.example.integradora5d.models.password_reset_token.PasswordResetTokenRepository;
import com.example.integradora5d.models.rol.BeanRol;
import com.example.integradora5d.models.rol.RolRepository;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import com.example.integradora5d.service.emailsend.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UsuarioService {
    private static final Pattern CURP_PATTERN =
            Pattern.compile("^[A-Z]{4}[0-9]{6}[HM][A-Z]{5}[A-Z0-9]{2}$");

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          UsuarioMapper usuarioMapper,
                          PasswordEncoder passwordEncoder,
                          RolRepository rolRepository,
                          PasswordResetTokenRepository tokenRepository,
                          EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    private String generarNumeroEmpleado(String curp, Long rolId) {
        long consecutivo = usuarioRepository.countByRolId(rolId) + 1;
        String prefijo = (curp != null && curp.length() >= 2)
                ? curp.substring(curp.length() - 2).toUpperCase()
                : "XX";

        String candidato = prefijo + String.format("%03d", consecutivo);
        while (usuarioRepository.existsByNumeroEmpleado(candidato)) {
            consecutivo++;
            candidato = prefijo + String.format("%03d", consecutivo);
        }

        return candidato;
    }

    private BeanUsuario getUsuarioOr404(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private String sanitizeRequired(String value, String message) {
        String sanitized = value == null ? "" : value.trim();
        if (sanitized.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return sanitized;
    }

    private String sanitizeCurp(String value) {
        String curp = sanitizeRequired(value, "La CURP es obligatoria").toUpperCase();
        if (curp.length() != 18 || !CURP_PATTERN.matcher(curp).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CURP invalida");
        }
        return curp;
    }

    private boolean isPasswordOnlyUpdate(UpdateUsuarioDTO dto) {
        if (dto == null) {
            return false;
        }

        boolean hasPassword = dto.getPassword() != null && !dto.getPassword().isBlank();
        if (!hasPassword) {
            return false;
        }

        return (dto.getNombre() == null || dto.getNombre().isBlank())
                && (dto.getCorreo() == null || dto.getCorreo().isBlank())
                && dto.getFechaNacimiento() == null
                && (dto.getCurp() == null || dto.getCurp().isBlank())
                && (dto.getNumeroEmpleado() == null || dto.getNumeroEmpleado().isBlank())
                && dto.getRolId() == null
                && (dto.getArea() == null || dto.getArea().isBlank());
    }

    @Transactional(readOnly = true)
    public List<UsuarioForClientDTO> getUsuarios() throws CustomNotContentException {
        List<BeanUsuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new CustomNotContentException("No hay usuarios");
        }
        return usuarioMapper.usuarioToUsuarioDto(usuarios);
    }

    @Transactional(readOnly = true)
    public UsuarioForClientDTO getById(Long id) {
        BeanUsuario usuario = getUsuarioOr404(id);
        return usuarioMapper.usuarioToUsuarioDto(usuario);
    }

    @Transactional
    public BeanUsuario createUsuario(CreateUsuarioDto dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya esta registrado");
        }
        if (usuarioRepository.existsByCurp(dto.getCurp())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La CURP ya esta registrada");
        }

        BeanUsuario usuario = usuarioMapper.toEntity(dto);
        BeanRol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no encontrado"));

        usuario.setRol(rol);
        usuario.setPrimerAcceso(true);
        usuario.setEstatus(true);
        usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setNumeroEmpleado(generarNumeroEmpleado(dto.getCurp(), dto.getRolId()));

        usuarioRepository.save(usuario);

        String token = UUID.randomUUID().toString();
        BeanPasswordResetToken resetToken = new BeanPasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);

        String link = "http://localhost:5173/reset-password?token=" + token;
        emailService.enviarLinkResetPassword(usuario.getCorreo(), link);

        return usuario;
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new CustomBadRequestException("Las contrasenas no coinciden");
        }

        BeanPasswordResetToken resetToken = tokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new CustomNotFoundException("Token invalido o no encontrado"));

        if (resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new CustomBadRequestException("El enlace de recuperacion ha expirado");
        }

        BeanUsuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuario.setPrimerAcceso(false);

        usuarioRepository.save(usuario);
        tokenRepository.delete(resetToken);
    }

    @Transactional
    public void guardarTokenDispositivo(String correo, String token) {
        BeanUsuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setTokenDispositivo(token);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<BeanUsuario> getTecnicos() {
        return usuarioRepository.findByRol_Nombre("TECNICO");
    }

    @Transactional(readOnly = true)
    public UsuarioForClientDTO getPerfil(String correo) {
        BeanUsuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return usuarioMapper.usuarioToUsuarioDto(usuario);
    }

    @Transactional
    public BeanUsuario update(Long id, UpdateUsuarioDTO dto) {
        BeanUsuario usuario = getUsuarioOr404(id);

        if (isPasswordOnlyUpdate(dto)) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
            usuario.setPrimerAcceso(false);
            return usuarioRepository.save(usuario);
        }

        String nombre = sanitizeRequired(dto.getNombre(), "El nombre es obligatorio");
        String correo = sanitizeRequired(dto.getCorreo(), "El correo es obligatorio");
        LocalDate fechaNacimiento = dto.getFechaNacimiento();
        String curp = sanitizeCurp(dto.getCurp());
        String numeroEmpleado = usuario.getNumeroEmpleado();
        String area = sanitizeRequired(dto.getArea(), "El area es obligatoria");
        Long rolId = dto.getRolId();

        if (fechaNacimiento == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de nacimiento es obligatoria");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de nacimiento no puede ser futura");
        }
        if (fechaNacimiento.isAfter(LocalDate.now().minusYears(18))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario debe tener al menos 18 años");
        }
        if (fechaNacimiento.isBefore(LocalDate.now().minusYears(100))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de nacimiento no es válida");
        }
        if (rolId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol es obligatorio");
        }

        if (usuarioRepository.existsByCorreoAndIdUsuarioNot(correo, usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya esta registrado");
        }
        if (usuarioRepository.existsByCurpAndIdUsuarioNot(curp, usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La CURP ya esta registrada");
        }

        BeanRol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no encontrado"));

        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setCurp(curp);
        usuario.setNumeroEmpleado(numeroEmpleado);
        usuario.setArea(area);
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Long id) {
        BeanUsuario usuario = getUsuarioOr404(id);

        if (usuarioRepository.tieneResguardos(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar, el usuario tiene bienes asignados"
            );
        }

        if (usuarioRepository.tieneMantenimientos(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar, el usuario tiene mantenimientos asignados"
            );
        }

        usuarioRepository.delete(usuario);
    }

    @Transactional
    public void solicitarRecuperacion(String correo) {
        BeanUsuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con el correo: " + correo));

        tokenRepository.deleteByUsuario(usuario);

        String token = UUID.randomUUID().toString();

        BeanPasswordResetToken resetToken = new BeanPasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);

        String link = "http://localhost:5173/reset-password?token=" + token;
        emailService.enviarLinkResetPassword(usuario.getCorreo(), link);
    }
}
