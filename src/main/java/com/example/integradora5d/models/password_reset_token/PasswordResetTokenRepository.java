package com.example.integradora5d.models.password_reset_token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<BeanPasswordResetToken, Long> {
    Optional<BeanPasswordResetToken> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM BeanPasswordResetToken t WHERE t.usuario.idUsuario = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}
