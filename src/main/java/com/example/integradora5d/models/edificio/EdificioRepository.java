package com.example.integradora5d.models.edificio;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EdificioRepository extends JpaRepository<BeanEdificio, Long> {
    Optional<BeanEdificio> findByNombreAndCampus_IdCampus(String nombre, Long campusId);
    List<BeanEdificio> findByCampus_IdCampus(Long campusId);
}
