package com.example.integradora5d.models.producto;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.modelo.BeanModelo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "producto")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_producto;

    private String nombre;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_modelo")
    private BeanModelo modelo;

    @Enumerated(EnumType.STRING)
    private ENUM_ESTATUS_PRODUCTO estatus;

    @OneToMany(mappedBy = "producto")
    private List<BeanActivo> activos;
}
