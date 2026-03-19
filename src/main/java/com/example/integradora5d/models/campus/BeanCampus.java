package com.example.integradora5d.models.campus;

import com.example.integradora5d.models.edificio.BeanEdificio;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "campus")
public class BeanCampus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCampus;

    private String nombre;

    @OneToMany(mappedBy = "campus")
    private List<BeanEdificio> edificio;
}
