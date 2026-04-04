package com.example.integradora5d.models.reporte_danio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReporteRepository extends JpaRepository<BeanReporte, Long> {

    List<BeanReporte> findByActivo_IdActivo(Long activoId);

    // Para dashboard: activos más reportados
    @Query("SELECT r.activo.idActivo, COUNT(r) as total " +
            "FROM BeanReporte r GROUP BY r.activo.idActivo " +
            "ORDER BY total DESC")
    List<Object[]> findActivosMasReportados();
    List<BeanReporte> findByEstatus(ENUM_REPORTEDANIO estatus);
}