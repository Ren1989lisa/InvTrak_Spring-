package com.example.integradora5d.models.usuario;

import com.example.integradora5d.models.resguardo.BeanResguardo;
import com.example.integradora5d.models.rol.BeanRol;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuario")
public class BeanUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;
    private String correo;
    private String contrasenia;
    private LocalDate fechaNacimiento;
    private String curp;
    private String numeroEmpleado;
    private String area;
    private Boolean estatus;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private BeanRol rol;

    @OneToMany(mappedBy = "usuario")
    private List<BeanResguardo> resguardos;
}
