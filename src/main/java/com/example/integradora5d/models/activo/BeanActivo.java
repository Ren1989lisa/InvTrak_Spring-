package com.example.integradora5d.models.activo;

import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.historial_activo.BeanHistorial;
import com.example.integradora5d.models.producto.BeanProducto;
import com.example.integradora5d.models.reporte_danio.BeanReporte;
import com.example.integradora5d.models.resguardo.BeanResguardo;
import com.example.integradora5d.models.solicitud_baja.BeanSolicitud;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "activo")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanActivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActivo;

    private String etiquetaBien;
    private String numeroSerie;
    private String descripcion;
    private LocalDate fechaAlta;
    private Double costo;

    @Enumerated(EnumType.STRING)
    private ENUM_ESTATUS_ACTIVO estatus;

    @ManyToOne
    @JoinColumn(name = "id_aula")
    private BeanAula aula;

    @OneToMany(mappedBy = "activo")
    private List<BeanResguardo> resguardos;

    @OneToMany(mappedBy = "activo")
    private List<BeanHistorial> historiales;

    @OneToMany(mappedBy = "activo")
    private List<BeanSolicitud> solicitudes;

    @OneToMany(mappedBy = "activo")
    private List<BeanReporte> reportes;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private BeanProducto producto;
}
