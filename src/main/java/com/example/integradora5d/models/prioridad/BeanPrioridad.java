package com.example.integradora5d.models.prioridad;

import com.example.integradora5d.models.mantenimiento.BeanMantenimiento;
import com.example.integradora5d.models.reporte_danio.BeanReporte; 
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "prioridad")
@Data
public class BeanPrioridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_prioridad;

    @Enumerated(EnumType.STRING)
    private String nombre;


    private String descripcion;

    @OneToMany(mappedBy = "prioridad", fetch = FetchType.LAZY)
    private List<BeanMantenimiento> mantenimientos;

    @OneToMany(mappedBy = "prioridad", fetch = FetchType.LAZY)
    private List<BeanReporte> reportes;
}