package com.example.integradora5d.models.mantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MantenimientoRepository extends JpaRepository<BeanMantenimiento, Long> {

    // Para el técnico: ver sus mantenimientos asignados
    List<BeanMantenimiento> findByTecnico_IdUsuario(Long tecnicoId);

    // Para dashboard: mantenimientos por técnico
    @Query("SELECT m.tecnico.nombre, COUNT(m) " +
            "FROM BeanMantenimiento m GROUP BY m.tecnico.nombre")
    List<Object[]> countByTecnico();

    // Para dashboard: tiempo promedio de atención
    @Query("SELECT AVG(DATEDIFF(m.fecha_fin, m.fecha_inicio)) " +
            "FROM BeanMantenimiento m WHERE m.fecha_fin IS NOT NULL")
    Double promedioTiempoAtencion();
}
