package com.example.integradora5d.models.modelo;

import com.example.integradora5d.models.marca.BeanMarca;
import jakarta.persistence.*;

public class BeanModelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_modelo;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_marca")
    private BeanMarca marca;
}
