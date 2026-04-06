package com.example.integradora5d.models.aula_laboratorio;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.campus.BeanCampus;
import com.example.integradora5d.models.edificio.BeanEdificio;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "aula_laboratorio")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanAula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAula;

    private String nombre;

    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_edificio")
    private BeanEdificio edificio;

    @OneToMany(mappedBy = "aula")
    @JsonIgnore
    private List<BeanActivo> activos;
}
