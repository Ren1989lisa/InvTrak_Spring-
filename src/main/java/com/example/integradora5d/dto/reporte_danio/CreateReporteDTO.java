package com.example.integradora5d.dto.reporte_danio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReporteDTO {

    @NotNull(message = "El activo es obligatorio")
    private Long activoId;

    @NotBlank(message = "El tipo de falla es obligatorio")
    private String tipoFalla;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "La prioridad es obligatoria")
    private Long prioridadId;
}