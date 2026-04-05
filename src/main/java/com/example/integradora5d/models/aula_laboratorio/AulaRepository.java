package com.example.integradora5d.models.aula_laboratorio;

import com.example.integradora5d.models.edificio.BeanEdificio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AulaRepository extends JpaRepository<BeanAula, Long> {
    Optional<BeanAula> findByNombreAndEdificio_IdEdificio(String nombre, Long edificioId);
    List<BeanAula> findByEdificio_IdEdificio(Long edificioId);
    boolean existsByEdificio(BeanEdificio edificio);

    boolean existsByEdificio_Campus_IdCampus(Long idCampus);
}
