package com.example.integradora5d.models.producto;

import com.example.integradora5d.models.modelo.BeanModelo;
import jakarta.persistence.*;

import java.util.List;

public class BeanProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_producto;

    private String nombre;
    private String descripcion;

    @OneToMany(mappedBy = "modelo")
    private List<BeanModelo> modelo;

    @Enumerated
    private ENUM_ESTATUS_PRODUCTO estatus;

}
