package com.example.integradora5d.dto.usuario;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUsuarioDTO {
    private String nombre;

    @Email(message = "Formato de correo inválido")
    private String correo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;
    private String curp;
    private Long rolId;
    private String area;
}