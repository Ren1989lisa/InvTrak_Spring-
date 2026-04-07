package com.example.integradora5d.models.password_reset_token;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<BeanPasswordResetToken, Long> {

    Optional<BeanPasswordResetToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUsuario(BeanUsuario usuario);
}