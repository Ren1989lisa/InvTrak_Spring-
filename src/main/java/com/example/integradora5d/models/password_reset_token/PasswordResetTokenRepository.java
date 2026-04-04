package com.example.integradora5d.models.password_reset_token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<BeanPasswordResetToken, Long> {
    Optional<BeanPasswordResetToken> findByToken(String token);
}
