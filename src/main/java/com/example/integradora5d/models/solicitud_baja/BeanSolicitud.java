package com.example.integradora5d.models.solicitud_baja;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.mantenimiento.BeanMantenimiento;
import com.example.integradora5d.models.usuario.BeanUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "solicitud_baja")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeanSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_solicitud;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    private LocalDate fecha_solicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus")
    private ENUM_SOLICITUD_BAJA estatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mantenimiento")
    private BeanMantenimiento mantenimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_activo")
    private BeanActivo activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_aprobacion")
    private BeanUsuario usuarioAprobacion;

}