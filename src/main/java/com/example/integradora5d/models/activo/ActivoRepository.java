package com.example.integradora5d.models.activo;

import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.producto.BeanProducto;
import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivoRepository extends JpaRepository<BeanActivo, Long> {
    boolean existsByNumeroSerie(String numeroSerie);
    boolean existsByProducto(BeanProducto producto);
    boolean existsByAula(BeanAula aula);
    int countByEtiquetaBienStartingWith(String prefijo);
}
