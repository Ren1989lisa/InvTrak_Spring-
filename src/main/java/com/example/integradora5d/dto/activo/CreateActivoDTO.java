package com.example.integradora5d.dto.activo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateActivoDTO {

    @NotBlank(message = "El número de serie es obligatorio")
    private String numeroSerie;

    @NotNull(message = "El tipo de activo es obligatorio")
    private Long productoId;

    @NotNull(message = "La fecha de alta es obligatoria")
    private LocalDate fechaAlta;

    @NotNull(message = "La ubicación es obligatoria")
    private Long aulaId;

    private String descripcion;

    private Double costo;
}
