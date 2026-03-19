package com.example.integradora5d.models.aula_laboratorio;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.campus.BeanCampus;
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
    private BeanCampus edificios;

    @OneToMany(mappedBy = "aula_laboratorio")
    private List<BeanActivo> activos;
}
