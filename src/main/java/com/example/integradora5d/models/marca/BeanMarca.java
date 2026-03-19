package com.example.integradora5d.models.marca;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class BeanMarca {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id_marca;

    private String nombre;
}
