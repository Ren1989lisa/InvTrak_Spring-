package com.example.integradora5d.models.producto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<BeanProducto, Long> {
    boolean existsByNombre(String nombre);

    Optional<BeanProducto> findByNombre(String nombre);
}
