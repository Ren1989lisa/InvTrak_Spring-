package com.example.integradora5d.models.reporte_danio;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.prioridad.BeanPrioridad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reporte_danio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeanReporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_reporte;

    private String tipo_falla;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private LocalDate fecha_reporte;

    @Enumerated(EnumType.STRING)
    private ENUM_REPORTEDANIO estatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prioridad")
    private BeanPrioridad prioridad;

    @ManyToOne
    @JoinColumn(name = "id_activo")
    private BeanActivo activo;

}
