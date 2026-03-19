package com.example.integradora5d.models.edificio;

import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.campus.BeanCampus;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "edificio")
public class BeanEdificio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEdificio;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_campus")
    private BeanCampus campus;

    @OneToMany(mappedBy = "edificio")
    private List<BeanAula> aulas;
}