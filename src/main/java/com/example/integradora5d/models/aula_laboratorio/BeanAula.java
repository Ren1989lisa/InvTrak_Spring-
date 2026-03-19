package com.example.integradora5d.models.aula_laboratorio;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.campus.BeanCampus;
import com.example.integradora5d.models.edificio.BeanEdificio;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "aula_laboratorio")
public class BeanAula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAula;

    @ManyToOne
    @JoinColumn(name = "id_edificio")
    private BeanEdificio edificio;

    @OneToMany(mappedBy = "aula")
    private List<BeanActivo> activos;
}
