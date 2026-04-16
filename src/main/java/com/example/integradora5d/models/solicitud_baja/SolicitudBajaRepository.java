package com.example.integradora5d.models.solicitud_baja;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudBajaRepository extends JpaRepository<BeanSolicitud, Long> {
    @Query("""
            SELECT s
            FROM BeanSolicitud s
            WHERE s.activo.idActivo = :activoId
              AND s.estatus = :estatus
            ORDER BY s.fecha_solicitud DESC, s.id_solicitud DESC
            """)
    List<BeanSolicitud> findByActivoAndEstatusDesc(
            @Param("activoId") Long activoId,
            @Param("estatus") ENUM_SOLICITUD_BAJA estatus
    );
}
