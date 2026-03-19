package com.example.integradora5d.models.rol;


import com.example.integradora5d.models.usuario.BeanUsuario;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "rol")
public class BeanRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    private String nombre;

    @OneToMany(mappedBy = "rol")
    private List<BeanUsuario> usuarios;
}