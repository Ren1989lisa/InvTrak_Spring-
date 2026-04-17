package com.example.integradora5d.models.activo;

import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.producto.BeanProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivoRepository extends JpaRepository<BeanActivo, Long> {
    boolean existsByNumeroSerie(String numeroSerie);
    boolean existsByNumeroSerieAndIdActivoNot(String numeroSerie, Long idActivo);
    boolean existsByProducto(BeanProducto producto);
    boolean existsByAula(BeanAula aula);
    int countByEtiquetaBienStartingWith(String prefijo);

    // Dashboard
    long countByEstatus(ENUM_ESTATUS_ACTIVO estatus);

    @Query("SELECT a.estatus, COUNT(a) FROM BeanActivo a GROUP BY a.estatus")
    List<Object[]> countGroupByEstatus();

    @Query("SELECT DISTINCT a FROM BeanActivo a LEFT JOIN FETCH a.resguardos r LEFT JOIN FETCH r.usuario")
    List<BeanActivo> findAllWithResguardos();

    @Query("SELECT DISTINCT a FROM BeanActivo a LEFT JOIN FETCH a.resguardos r LEFT JOIN FETCH r.usuario WHERE a.estatus = :estatus")
    List<BeanActivo> findByEstatusWithResguardos(@org.springframework.data.repository.query.Param("estatus") ENUM_ESTATUS_ACTIVO estatus);

    List<BeanActivo> findByEstatus(ENUM_ESTATUS_ACTIVO estatus);

    // Activos resguardados por un usuario (confirmados y sin devolucion)
    @Query("SELECT DISTINCT r.activo FROM BeanResguardo r " +
           "WHERE r.usuario.idUsuario = :usuarioId " +
           "AND r.confirmado = true " +
           "AND r.fechaDevolucion IS NULL")
    List<BeanActivo> findActivosResguardadosByUsuario(Long usuarioId);

    // Activos en mantenimiento asignados a un tecnico
    @Query("SELECT DISTINCT m.reporte.activo FROM BeanMantenimiento m " +
           "WHERE m.tecnico.idUsuario = :tecnicoId " +
           "AND m.estatus IN ('PENDIENTE', 'EN_PROCESO')")
    List<BeanActivo> findActivosEnMantenimientoByTecnico(Long tecnicoId);
}
