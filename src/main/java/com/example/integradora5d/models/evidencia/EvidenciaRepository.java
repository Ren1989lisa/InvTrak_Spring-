package com.example.integradora5d.models.evidencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvidenciaRepository extends JpaRepository<BeanEvidencia, Long> {
    boolean existsByMantenimiento_Id_mantenimiento(Long mantenimientoId);
}
