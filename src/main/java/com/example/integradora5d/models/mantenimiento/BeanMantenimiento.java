package com.example.integradora5d.models.mantenimiento;

import com.example.integradora5d.models.prioridad.BeanPrioridad;
import com.example.integradora5d.models.reporte_danio.BeanReporte;
import com.example.integradora5d.models.usuario.BeanUsuario;
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
    @JoinColumn(name = "id_prioridad")
    private BeanPrioridad prioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reporte")
    private BeanReporte reporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tecnico")
    private BeanUsuario tecnico;

    @Enumerated(EnumType.STRING)
    private ENUM_TIPO_MANTENIMIENTO tipo;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String accionesRealizadas;

    @Column(columnDefinition = "TEXT")
    private String piezasUtilizadas;

    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;

    @Enumerated(EnumType.STRING)
    private ENUM_MANTENIMIENTO estatus;
}