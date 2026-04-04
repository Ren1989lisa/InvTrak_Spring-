package com.example.integradora5d.dto.mantenimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtenderMantenimientoDTO {

    @NotNull
    private Long mantenimientoId;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "Las acciones realizadas son obligatorias")
    private String accionesRealizadas;

    private String piezasUtilizadas;
}
