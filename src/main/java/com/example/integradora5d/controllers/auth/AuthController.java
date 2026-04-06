package com.example.integradora5d.controllers.auth;

import com.example.integradora5d.dto.auth.LoginRequestDTO;
import com.example.integradora5d.dto.auth.LoginResponseDTO;
import com.example.integradora5d.dto.auth.ResetPasswordDTO;
import com.example.integradora5d.service.auth.AuthService;
import com.example.integradora5d.service.usuario.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin({"*"})
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO body) {
        return ResponseEntity.ok(authService.login(body));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        usuarioService.resetPassword(dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/dispositivo-token")
    public ResponseEntity<Void> registrarToken(@RequestBody Map<String, String> body,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        usuarioService.guardarTokenDispositivo(userDetails.getUsername(), body.get("token"));
        return ResponseEntity.ok().build();
    }
}