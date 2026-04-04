package com.example.integradora5d.models.modelo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModeloRepository extends JpaRepository<BeanModelo, Long> {
    boolean existsByNombreAndMarcaId_marca(String nombre, Long marcaId);

    List<BeanModelo> findByMarca_Id_marca(Long marcaId);
    Optional<BeanModelo> findByNombreAndMarca_Id_marca(String nombre, Long marcaId);
}