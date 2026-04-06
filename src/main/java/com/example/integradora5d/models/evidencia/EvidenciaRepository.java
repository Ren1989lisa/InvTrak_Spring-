package com.example.integradora5d.models.evidencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenciaRepository extends JpaRepository<BeanEvidencia, Long> {
    @Query("SELECT COUNT(e) > 0 FROM BeanEvidencia e WHERE e.mantenimiento.id_mantenimiento = :mantenimientoId")
    boolean existsByMantenimientoId(@Param("mantenimientoId") Long mantenimientoId);}
