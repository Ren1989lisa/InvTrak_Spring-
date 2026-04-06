package com.example.integradora5d.models.modelo;

import com.example.integradora5d.models.marca.BeanMarca;
import com.example.integradora5d.models.producto.BeanProducto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "modelo")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanModelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_modelo;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_marca")
    private BeanMarca marca;

    @OneToMany(mappedBy = "modelo")
    @JsonIgnore
    private List<BeanProducto> productos;
}
