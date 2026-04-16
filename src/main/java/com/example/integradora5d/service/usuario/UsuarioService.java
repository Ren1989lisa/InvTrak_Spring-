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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {
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
        long count = usuarioRepository.countByRolId(rolId);
        String consecutivo = String.format("%03d", count + 1);
        String prefijo = (curp != null && curp.length() >= 2)
                ? curp.substring(curp.length() - 2)
                : "XX";
        return prefijo + consecutivo;
    }

    @Transactional(readOnly = true)
    public List<UsuarioForClientDTO> getUsuarios() throws CustomNotContentException {
        List<BeanUsuario> usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            throw new CustomNotContentException("No hay usuarios");
        }

        return usuarioMapper.usuarioToUsuarioDto(usuarios);
    }

    @Transactional
    public BeanUsuario createUsuario(CreateUsuarioDto dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("El correo ya esta registrado");
        }

        if (usuarioRepository.existsByCurp(dto.getCurp())) {
            throw new RuntimeException("La CURP ya esta registrada");
        }

        BeanUsuario usuario = usuarioMapper.toEntity(dto);
        BeanRol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuarioMapper.usuarioToUsuarioDto(usuario);
    }

    @Transactional
    public BeanUsuario update(Long id, UpdateUsuarioDTO dto) {
        BeanUsuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            usuario.setNombre(dto.getNombre());
        }

        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()) {
            if (usuarioRepository.existsByCorreo(dto.getCorreo())
                    && !dto.getCorreo().equals(usuario.getCorreo())) {
                throw new RuntimeException("El correo ya esta registrado");
            }
            usuario.setCorreo(dto.getCorreo());
        }

        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento());
        }

        if (dto.getCurp() != null && !dto.getCurp().isBlank()) {
            if (usuarioRepository.existsByCurp(dto.getCurp())
                    && !dto.getCurp().equals(usuario.getCurp())) {
                throw new RuntimeException("La CURP ya esta registrada");
            }
            usuario.setCurp(dto.getCurp());
        }

        if (dto.getRolId() != null) {
            BeanRol rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            usuario.setRol(rol);
        }

        if (dto.getArea() != null && !dto.getArea().isBlank()) {
            usuario.setArea(dto.getArea());
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Long id) {
        BeanUsuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuarioRepository.tieneResguardos(id)) {
            throw new RuntimeException("No se puede eliminar, el usuario tiene bienes asignados");
        }

        if (usuarioRepository.tieneMantenimientos(id)) {
            throw new RuntimeException("No se puede eliminar, el usuario tiene mantenimientos asignados");
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
