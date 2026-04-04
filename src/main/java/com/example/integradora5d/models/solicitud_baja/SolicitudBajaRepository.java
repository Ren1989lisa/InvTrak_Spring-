package com.example.integradora5d.models.solicitud_baja;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudBajaRepository extends JpaRepository<BeanSolicitud, Long> {
}
