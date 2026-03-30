package com.example.integradora5d.models.mantenimiento;

import com.example.integradora5d.models.prioridad.BeanPrioridad;
import com.example.integradora5d.models.reporte_danio.BeanReporte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mantenimiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeanMantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_mantenimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prioridad", nullable = false)
    private BeanPrioridad prioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reporte")
    private BeanReporte reporte;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;

    @Enumerated(EnumType.STRING)
    private ENUM_MANTENIMIENTO estatus;
}