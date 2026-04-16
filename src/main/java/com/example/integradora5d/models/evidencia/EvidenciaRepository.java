package com.example.integradora5d.models.evidencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaRepository extends JpaRepository<BeanEvidencia, Long> {
    @Query("SELECT COUNT(e) > 0 FROM BeanEvidencia e WHERE e.mantenimiento.id_mantenimiento = :mantenimientoId")
    boolean existsByMantenimientoId(@Param("mantenimientoId") Long mantenimientoId);

    @Query("""
            SELECT e
            FROM BeanEvidencia e
            WHERE e.reporte.id_reporte = :reporteId
            ORDER BY e.fecha_subida DESC, e.id_foto_resguardo DESC
            """)
    List<BeanEvidencia> findByReporteId(@Param("reporteId") Long reporteId);

    void deleteByResguardo_IdResguardo(Long resguardoId);
}
