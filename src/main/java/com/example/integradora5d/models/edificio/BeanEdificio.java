package com.example.integradora5d.models.edificio;

import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.campus.BeanCampus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "edificio")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanEdificio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEdificio;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_campus")
    private BeanCampus campus;

    @OneToMany(mappedBy = "edificio")
    @JsonIgnore
    private List<BeanAula> aulas;
}