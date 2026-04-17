package com.example.integradora5d.models.prioridad;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrioridadRepository extends JpaRepository<BeanPrioridad, Long> {
    @Query(value = "SELECT * FROM prioridad ORDER BY id_prioridad ASC LIMIT 1", nativeQuery = true)
    Optional<BeanPrioridad> findFirstPrioridad();
}
