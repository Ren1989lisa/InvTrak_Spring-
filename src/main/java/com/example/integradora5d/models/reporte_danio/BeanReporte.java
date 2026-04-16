package com.example.integradora5d.models.reporte_danio;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.prioridad.BeanPrioridad;
import com.example.integradora5d.models.evidencia.BeanEvidencia;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.EAGER) // Cambiado a EAGER para ver la prioridad en Android
    @JoinColumn(name = "id_prioridad")
    private BeanPrioridad prioridad;

    @ManyToOne
    @JoinColumn(name = "id_activo")
    private BeanActivo activo;

    // Relación corregida para recuperar las fotos del reporte
    @OneToMany(mappedBy = "reporte", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BeanEvidencia> evidencias;
}