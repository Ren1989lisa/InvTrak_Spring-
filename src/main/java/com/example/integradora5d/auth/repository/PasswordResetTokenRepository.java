package com.example.integradora5d.auth.repository;

import com.example.integradora5d.auth.entity.PasswordResetToken;
import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUsuario(BeanUsuario usuario);
}