package com.example.integradora5d.models.campus;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampusRepository extends JpaRepository<BeanCampus, Long> {
    Optional<BeanCampus> findByNombre(String nombre);
}
