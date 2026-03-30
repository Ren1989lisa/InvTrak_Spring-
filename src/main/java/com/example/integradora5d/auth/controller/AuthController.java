package com.example.integradora5d.auth.controller;

import com.example.integradora5d.auth.dto.ForgotPasswordDTO;
import com.example.integradora5d.auth.dto.ResetPasswordDTO;
import com.example.integradora5d.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        authService.forgotPassword(dto);
        return ResponseEntity.ok("Correo enviado");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok("Contraseña actualizada");
    }
}