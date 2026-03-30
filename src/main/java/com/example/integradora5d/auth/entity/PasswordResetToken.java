package com.example.integradora5d.auth.entity;

import com.example.integradora5d.models.usuario.BeanUsuario;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    private LocalDateTime expiration;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private BeanUsuario usuario;
}
