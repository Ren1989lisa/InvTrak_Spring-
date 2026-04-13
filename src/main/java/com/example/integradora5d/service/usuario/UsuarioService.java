package com.example.integradora5d.service.usuario;

import com.example.integradora5d.dto.auth.ResetPasswordDTO;
import com.example.integradora5d.dto.usuario.CreateUsuarioDto;
import com.example.integradora5d.dto.usuario.UpdateUsuarioDTO;
import com.example.integradora5d.dto.usuario.UsuarioForClientDTO;
import com.example.integradora5d.error.errorTypes.CustomNotContentException;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;
    private UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    private void validarCurpConDatos(String curp, String nombre, LocalDate fechaNacimiento) {
        // Los primeros 4 caracteres vienen del nombre/apellidos
        // Los siguientes 6 son la fecha: AAMMDD
        String fechaEnCurp = curp.substring(4, 10);
        String fechaEsperada = fechaNacimiento.format(DateTimeFormatter.ofPattern("yyMMdd"));

        if (!fechaEnCurp.equals(fechaEsperada)) {
            throw new RuntimeException("La CURP no corresponde a la fecha de nacimiento");
        }
    }

    private String generarNumeroEmpleado(String curp, Long rolId) {
        long count = usuarioRepository.countByRolId(rolId);
        String consecutivo = String.format("%03d", count + 1);
        return curp.substring(curp.length() - 2) + consecutivo;
    }


    public UsuarioService(UsuarioRepository usuarioRepository,
                          UsuarioMapper usuarioMapper,
                          PasswordEncoder passwordEncoder,
                          RolRepository rolRepository, PasswordResetTokenRepository tokenRepository, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<UsuarioForClientDTO> getUsuarios() throws CustomNotContentException {
        List<BeanUsuario> usuarios = usuarioRepository.findAll();

        if(usuarios.isEmpty()){
            throw new CustomNotContentException("No hay usuarios");
        }

        return usuarioMapper.usuarioToUsuarioDto(usuarios);
    }

    @Transactional
    public BeanUsuario createUsuario(CreateUsuarioDto dto) {

        //Validaciones
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (usuarioRepository.existsByCurp(dto.getCurp())) {
            throw new RuntimeException("La CURP ya está registrada");
        }

        // Mapeo y asignación
        BeanUsuario usuario = usuarioMapper.toEntity(dto);
        BeanRol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setRol(rol);
        usuario.setPrimerAcceso(true);
        usuario.setEstatus(true);

        // Contraseña aleatoria y segura
        usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setNumeroEmpleado(generarNumeroEmpleado(dto.getCurp(), dto.getRolId()));

        // Guardado
        usuarioRepository.save(usuario);

        //Creación del Token
        String token = UUID.randomUUID().toString();
        BeanPasswordResetToken resetToken = new BeanPasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(resetToken);

        // 5. Envío Asíncrono
        String link = "http://localhost:8085/api/auth/reset-password?token=" + token;
        emailService.enviarLinkResetPassword(usuario.getCorreo(), link);

        return usuario;
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {

        // Validar que las contraseñas coincidan antes de cualquier operación de BD
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // Buscar token
        BeanPasswordResetToken resetToken = tokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido o no encontrado"));

        // Validar expiración
        if (resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            // Opcional: eliminar el token expirado aquí
            tokenRepository.delete(resetToken);
            throw new RuntimeException("El enlace de recuperación ha expirado");
        }

        // Obtener y actualizar usuario
        BeanUsuario usuario = resetToken.getUsuario();

        // Encriptar la nueva contraseña
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        usuario.setPassword(encodedPassword);
        usuario.setPrimerAcceso(false);

        // Persistir cambios
        usuarioRepository.save(usuario);

        // Eliminar token (Asegura que el enlace sea de un solo uso)
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

    // Ver perfil propio
    @Transactional(readOnly = true)
    public UsuarioForClientDTO getPerfil(String correo) {
        BeanUsuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuarioMapper.usuarioToUsuarioDto(usuario);
    }

    // Admin edita cualquier usuario
    @Transactional
    public BeanUsuario update(Long id, UpdateUsuarioDTO dto) {
        BeanUsuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            usuario.setNombre(dto.getNombre());
        }

        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()) {
            if (usuarioRepository.existsByCorreo(dto.getCorreo()) &&
                    !dto.getCorreo().equals(usuario.getCorreo())) {
                throw new RuntimeException("El correo ya está registrado");
            }
            usuario.setCorreo(dto.getCorreo());
        }

        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento());
        }

        if (dto.getCurp() != null && !dto.getCurp().isBlank()) {
            if (usuarioRepository.existsByCurp(dto.getCurp()) &&
                    !dto.getCurp().equals(usuario.getCurp())) {
                throw new RuntimeException("La CURP ya está registrada");
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

    // Admin elimina usuario
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
        // Buscar el usuario
        BeanUsuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con el correo: " + correo));

        // Limpiar tokens anteriores
        tokenRepository.deleteByUsuario(usuario);

        // Generar nuevo token con expiración de 15 minutos
        String token = UUID.randomUUID().toString();

        BeanPasswordResetToken resetToken = new BeanPasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(resetToken);

        // Enviar el correo con el nuevo link
        String link = "http://localhost:8085/api/auth/reset-password?token=" + token;
        emailService.enviarLinkResetPassword(usuario.getCorreo(), link);
    }
}
