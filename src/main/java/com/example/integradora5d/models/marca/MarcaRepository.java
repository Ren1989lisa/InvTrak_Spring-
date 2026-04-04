package com.example.integradora5d.models.marca;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarcaRepository extends JpaRepository<BeanMarca, Long> {
    boolean existsByNombre(String nombre);
    Optional<BeanMarca> findByNombre(String nombre);
}