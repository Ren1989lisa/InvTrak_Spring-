package com.example.integradora5d.models.marca;

import com.example.integradora5d.models.modelo.BeanModelo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "marca")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanMarca {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id_marca;

    private String nombre;

    @OneToMany(mappedBy = "marca")
    @JsonIgnore
    private List<BeanModelo> modelos;
}
