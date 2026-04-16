package com.example.integradora5d.dto.mantenimiento;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitarBajaDTO {

    @NotNull(message = "El mantenimientoId es obligatorio")
    private Long mantenimientoId;

    private String observaciones;
}

