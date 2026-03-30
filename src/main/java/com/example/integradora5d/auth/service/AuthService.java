package com.example.integradora5d.auth.service;

import com.example.integradora5d.auth.dto.ForgotPasswordDTO;
import com.example.integradora5d.auth.dto.LoginRequestDTO;
import com.example.integradora5d.auth.dto.LoginResponseDTO;
import com.example.integradora5d.auth.dto.ResetPasswordDTO;
import com.example.integradora5d.auth.entity.PasswordResetToken;
import com.example.integradora5d.auth.repository.PasswordResetTokenRepository;
import com.example.integradora5d.error.errorTypes.CustomBadRequestException;
import com.example.integradora5d.error.errorTypes.CustomNotFoundException;
import com.example.integradora5d.mappers.UsuarioMapper;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import com.example.integradora5d.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    private UsuarioRepository usuarioRepository;
    private UsuarioMapper usuarioMapper;
    private PasswordResetTokenRepository tokenRepository;
    private EmailService emailService;

    public LoginResponseDTO login(LoginRequestDTO dto) {

        BeanUsuario user = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new CustomNotFoundException("Usuario no existe"));

        if (!user.getEstatus()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponseDTO(
                token,
                user.getRol().getNombre(),
                usuarioMapper.usuarioToUsuarioDto(user)
        );
    }

    public void forgotPassword(ForgotPasswordDTO dto) {

        BeanUsuario user = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new CustomNotFoundException("Usuario no encontrado"));

        // generar token
        String tokenStr = UUID.randomUUID().toString();

        // crear entidad token
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenStr);
        token.setUsuario(user);
        token.setExpiration(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(token);

        // generar link
        String link = "http://localhost:3000/reset-password?token=" + tokenStr;

        // enviar correo
        emailService.sendEmail(
                user.getCorreo(),
                "Recuperación de contraseña",
                "Da clic en el siguiente enlace para cambiar tu contraseña:\n" + link
        );
    }

    public void resetPassword(ResetPasswordDTO dto) {

        PasswordResetToken token = tokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new CustomNotFoundException("Token inválido"));

        // Validar expiración
        if (token.getExpiration().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new CustomBadRequestException("Token expirado");
        }

        if (!dto.getNuevaPassword().equals(dto.getConfirmarPassword())) {
            throw new CustomBadRequestException("Las contraseñas no coinciden");
        }

        BeanUsuario user = token.getUsuario();

        if (!user.getEstatus()) {
            throw new CustomBadRequestException("Usuario inactivo");
        }

        // Validación básica
        if (dto.getNuevaPassword().length() < 6) {
            throw new CustomBadRequestException("La contraseña debe tener al menos 6 caracteres");
        }

        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(dto.getNuevaPassword()));

        usuarioRepository.save(user);

        // Eliminar token
        tokenRepository.deleteByUsuario(user);
    }
}
