package com.example.integradora5d.dto.resguardo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateResguardoDTO {

    @NotNull(message = "El activo es obligatorio")
    private Long activoId;

    @NotNull(message = "El empleado es obligatorio")
    private Long usuarioId;

    private String observaciones;
}