package com.example.integradora5d.models.historial_activo;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.rol.BeanRol;
import com.example.integradora5d.models.usuario.BeanUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "historial")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BeanHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_historial;

    private String estatus_anterior;
    private String estatus_nuevo;
    private String fecha_cambio;
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "id_activo")
    private BeanActivo activo;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private BeanUsuario usuario;
}
