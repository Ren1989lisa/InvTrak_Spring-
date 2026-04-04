package com.example.integradora5d.dto.mantenimiento;

import com.example.integradora5d.models.mantenimiento.ENUM_TIPO_MANTENIMIENTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignarMantenimientoDTO {

    @NotNull(message = "El reporte es obligatorio")
    private Long reporteId;

    @NotNull(message = "El técnico es obligatorio")
    private Long tecnicoId;

    @NotNull(message = "El tipo es obligatorio")
    private ENUM_TIPO_MANTENIMIENTO tipo;

    @NotNull(message = "La prioridad es obligatoria")
    private Long prioridadId;
}