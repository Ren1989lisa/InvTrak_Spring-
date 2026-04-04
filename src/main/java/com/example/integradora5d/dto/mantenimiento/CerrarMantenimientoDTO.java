package com.example.integradora5d.dto.mantenimiento;

import com.example.integradora5d.models.mantenimiento.ENUM_MANTENIMIENTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CerrarMantenimientoDTO {

    @NotNull
    private Long mantenimientoId;

    @NotNull(message = "El estatus final es obligatorio")
    private ENUM_MANTENIMIENTO estatusFinal; // REPARADO o IRREPARABLE

    private String observaciones;
}
