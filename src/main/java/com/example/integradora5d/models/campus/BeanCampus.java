package com.example.integradora5d.models.campus;

import com.example.integradora5d.models.edificio.BeanEdificio;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "campus")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanCampus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCampus;

    private String nombre;

    @OneToMany(mappedBy = "campus")
    @JsonIgnore
    private List<BeanEdificio> edificio;
}
