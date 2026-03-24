package com.example.integradora5d.models.solicitud_baja;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudRepository extends JpaRepository<BeanSolicitud, Long> {
}
