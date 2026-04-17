package com.example.integradora5d.models.resguardo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResguardoRepository extends JpaRepository<BeanResguardo, Long> {
    List<BeanResguardo> findByUsuario_IdUsuario(Long usuarioId);

    List<BeanResguardo> findByUsuario_IdUsuarioOrderByIdResguardoDesc(Long usuarioId);

    List<BeanResguardo> findAllByOrderByIdResguardoDesc();

    Optional<BeanResguardo> findTopByActivo_IdActivoAndConfirmadoFalseOrderByIdResguardoDesc(Long activoId);

    Optional<BeanResguardo> findTopByActivo_IdActivoAndUsuario_IdUsuarioAndConfirmadoFalseOrderByIdResguardoDesc(
            Long activoId,
            Long usuarioId
    );

    Optional<BeanResguardo> findTopByUsuario_IdUsuarioAndActivo_IdActivoAndConfirmadoFalseOrderByIdResguardoDesc(
            Long usuarioId,
            Long activoId
    );

    Optional<BeanResguardo> findTopByUsuario_IdUsuarioAndActivo_IdActivoAndConfirmadoTrueOrderByIdResguardoDesc(
            Long usuarioId,
            Long activoId
    );
}
