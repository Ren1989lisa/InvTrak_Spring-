package com.example.integradora5d.service.auth;

import com.example.integradora5d.dto.auth.LoginRequestDTO;
import com.example.integradora5d.dto.auth.LoginResponseDTO;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import com.example.integradora5d.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getPassword())
        );

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtService.generateAccessToken(authentication.getName(), authorities);

        BeanUsuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos"));

        // CAMBIO AQUÍ: Se usa getIdUsuario() porque así se llama en tu BeanUsuario
        return new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getPrimerAcceso(),
                authorities,
                usuario.getIdUsuario()
        );
    }
}