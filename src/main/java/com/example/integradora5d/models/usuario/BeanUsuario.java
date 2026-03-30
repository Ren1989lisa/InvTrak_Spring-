package com.example.integradora5d.models.usuario;

import com.example.integradora5d.auth.entity.PasswordResetToken;
import com.example.integradora5d.models.historial_activo.BeanHistorial;
import com.example.integradora5d.models.resguardo.BeanResguardo;
import com.example.integradora5d.models.rol.BeanRol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeanUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;

    @Column(unique = true)
    private String correo;

    // *
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private LocalDate fechaNacimiento;

    @Column(unique = true)
    private String curp;

    @Column(unique = true)
    private String numeroEmpleado;

    private String area;
    private Boolean estatus = Boolean.TRUE;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private BeanRol rol;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<BeanResguardo> resguardos;

    @OneToMany(mappedBy = "usuario")
    private List<PasswordResetToken>  tokens;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<BeanHistorial> historiales;
}
