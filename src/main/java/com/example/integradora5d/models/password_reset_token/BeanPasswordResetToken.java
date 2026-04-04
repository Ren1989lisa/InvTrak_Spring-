package com.example.integradora5d.models.password_reset_token;

import com.example.integradora5d.models.usuario.BeanUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BeanPasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    private LocalDateTime fechaExpiracion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private BeanUsuario usuario;
}
