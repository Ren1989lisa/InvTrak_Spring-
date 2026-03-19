package com.example.integradora5d.models.activo;

import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.producto.BeanProducto;
import com.example.integradora5d.models.resguardo.BeanResguardo;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "activo")
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
}
