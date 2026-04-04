package com.example.integradora5d.dto.ubicacion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUbicacionDTO {

    // Campus: existente o nuevo
    private Long campusId;
    private String campusNombre;

    // Edificio: existente o nuevo
    private Long edificioId;
    private String edificioNombre;

    // Aula: siempre se escribe
    @NotBlank(message = "El nombre del aula es obligatorio")
    private String aulaNombre;

    private String descripcion;
}
