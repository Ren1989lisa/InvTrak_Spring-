package com.example.integradora5d.dto.usuario;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioForClientDTO {
    private Long idUsuario;
    private String nombre;
    private String correo;
    private String numeroEmpleado;
    private String area;
    private LocalDate fechaNacimiento;
    private Boolean estatus;
    private String curp;
    private String rol;

}
