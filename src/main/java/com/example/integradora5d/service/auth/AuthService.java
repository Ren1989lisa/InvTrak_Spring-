package com.example.integradora5d.service.auth;

import com.example.integradora5d.dto.auth.LoginRequestDTO;
import com.example.integradora5d.dto.auth.LoginResponseDTO;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import com.example.integradora5d.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private static final int MAX_INTENTOS = 3;
    private static final int MINUTOS_BLOQUEO = 3;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String correo = normalizeEmail(dto.getCorreo());
        String password = dto.getPassword() == null ? "" : dto.getPassword().trim();

        BeanUsuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));

        // Verificar bloqueo
        if (usuario.getBloqueadoHasta() != null && LocalDateTime.now().isBefore(usuario.getBloqueadoHasta())) {
            long segundosRestantes = java.time.Duration.between(LocalDateTime.now(), usuario.getBloqueadoHasta()).getSeconds();
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Cuenta bloqueada. Intenta en " + segundosRestantes + " segundos.");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, password)
            );
        } catch (AuthenticationException ex) {
            // Incrementar intentos fallidos
            int intentos = (usuario.getIntentosFallidos() == null ? 0 : usuario.getIntentosFallidos()) + 1;
            usuario.setIntentosFallidos(intentos);
            if (intentos >= MAX_INTENTOS) {
                usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
                usuario.setIntentosFallidos(0);
                usuarioRepository.save(usuario);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "Demasiados intentos fallidos. Cuenta bloqueada por " + MINUTOS_BLOQUEO + " minutos.");
            }
            usuarioRepository.save(usuario);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas. Intentos restantes: " + (MAX_INTENTOS - intentos));
        }

        // Login exitoso: resetear intentos
        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtService.generateAccessToken(authentication.getName(), authorities);

        if (passwordEncoder.upgradeEncoding(usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(password));
        }
        usuarioRepository.save(usuario);

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getPrimerAcceso(),
                authorities,
                usuario.getIdUsuario()
        );
    }

    private String normalizeEmail(String correo) {
        if (correo == null) {
            return "";
        }
        return correo.trim().toLowerCase();
    }
}
