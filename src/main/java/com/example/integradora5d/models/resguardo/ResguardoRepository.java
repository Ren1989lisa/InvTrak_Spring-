package com.example.integradora5d.models.resguardo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResguardoRepository extends JpaRepository<BeanResguardo, Long> {
    List<BeanResguardo> findByUsuario_IdUsuario(Long usuarioId);
    Optional<BeanResguardo> findByActivo_IdActivoAndConfirmadoFalse(Long activoId);
}