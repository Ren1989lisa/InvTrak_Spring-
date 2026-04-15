package com.example.integradora5d.dto.usuario;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUsuarioDTO {
    private String nombre;

    @Email(message = "Formato de correo inválido")
    private String correo;

    private LocalDate fechaNacimiento;
    private String curp;
    private String numeroEmpleado;
    private Long rolId;
    private String area;
    private String password;
}
