package com.example.integradora5d.models.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloRepository extends JpaRepository<BeanModelo, Long> {

    @Query("SELECT m FROM BeanModelo m WHERE m.nombre = :nombre AND m.marca.id_marca = :marcaId")
    Optional<BeanModelo> findByNombreAndMarcaId(@Param("nombre") String nombre, @Param("marcaId") Long marcaId);

    @Query("SELECT m FROM BeanModelo m WHERE m.marca.id_marca = :marcaId")
    List<BeanModelo> findByMarcaId(@Param("marcaId") Long marcaId);
}