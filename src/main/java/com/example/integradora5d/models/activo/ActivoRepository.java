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

    List<BeanActivo> findByEstatus(ENUM_ESTATUS_ACTIVO estatus);
}
