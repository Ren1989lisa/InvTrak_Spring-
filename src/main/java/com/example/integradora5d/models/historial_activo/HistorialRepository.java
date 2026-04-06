package com.example.integradora5d.models.historial_activo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<BeanHistorial, Long> {

    @Query("SELECT h FROM BeanHistorial h WHERE h.activo.idActivo = :activoId ORDER BY h.fecha_cambio DESC")
    List<BeanHistorial> findByActivoId(@Param("activoId") Long activoId);

    @Query("SELECT h FROM BeanHistorial h ORDER BY h.fecha_cambio DESC")
    List<BeanHistorial> findAllOrdenado();
}