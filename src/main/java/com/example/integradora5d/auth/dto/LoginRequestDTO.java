package com.example.integradora5d.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "El email es requerido")
    @Email(message = "Formato de correo no valido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    private String password;
}