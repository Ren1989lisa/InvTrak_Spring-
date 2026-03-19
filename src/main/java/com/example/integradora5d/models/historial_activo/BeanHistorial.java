package com.example.integradora5d.models.historial_activo;

import com.example.integradora5d.models.usuario.BeanUsuario;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

public class BeanHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_historial;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_activo;

    private String estatus_anterior;
    private String estatus_nuevo;
    private String fecha_cambio;
    private String motivo;

    @OneToMany(mappedBy = "usuario")
    private List<BeanUsuario> id_usuario;
}
