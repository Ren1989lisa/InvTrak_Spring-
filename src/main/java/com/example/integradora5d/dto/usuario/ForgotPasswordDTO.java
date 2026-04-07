package com.example.integradora5d.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordDTO {
    @NotBlank(message = "El correo es obligatorio")
    private String correo;
}