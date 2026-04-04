package com.example.integradora5d.models.historial_activo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<BeanHistorial, Long> {
    List<BeanHistorial> findByActivo_IdActivoOrderByFecha_cambioDesc(Long activoId);
    List<BeanHistorial> findAllByOrderByFecha_cambioDesc();
}