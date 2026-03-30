package com.example.integradora5d.models.evidencia;

import com.example.integradora5d.models.mantenimiento.BeanMantenimiento;
import com.example.integradora5d.models.reporte_danio.BeanReporte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evidencia")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeanEvidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_foto_resguardo;

    private String ruta_archivo;

    private LocalDateTime fecha_subida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private ENUM_TIPO_EVIDENCIA tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_archivo")
    private ENUM_TIPO_ARCHIVO tipo_archivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mantenimiento")
    private BeanMantenimiento mantenimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reporte")
    private BeanReporte reporte;
}