package com.example.integradora5d.dto.producto;

import com.example.integradora5d.models.producto.ENUM_ESTATUS_PRODUCTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductoDTO {
    private String descripcion;
    private ENUM_ESTATUS_PRODUCTO estatus;
}