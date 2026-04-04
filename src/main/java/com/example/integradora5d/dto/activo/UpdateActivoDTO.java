package com.example.integradora5d.dto.activo;

import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateActivoDTO {
    private String descripcion;
    private Double costo;
    private Long aulaId;
    private ENUM_ESTATUS_ACTIVO estatus;
}