package com.example.integradora5d.models.rol;


import com.example.integradora5d.models.usuario.BeanUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "rol")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeanRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    private String nombre;

    @OneToMany(mappedBy = "rol")
    @JsonIgnore
    private List<BeanUsuario> usuarios;
}