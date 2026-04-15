package com.example.integradora5d.dto.activo;

import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateActivoDTO {
    @NotBlank(message = "El numero de serie es obligatorio")
    private String numeroSerie;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La fecha de alta es obligatoria")
    private LocalDate fechaAlta;

    @NotNull(message = "La ubicacion es obligatoria")
    private Long aulaId;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "El costo es obligatorio")
    @Positive(message = "El costo debe ser mayor a 0")
    private Double costo;

    @NotNull(message = "El estatus es obligatorio")
    private ENUM_ESTATUS_ACTIVO estatus;
}
