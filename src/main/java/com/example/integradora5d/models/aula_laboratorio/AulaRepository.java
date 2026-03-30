package com.example.integradora5d.models.aula_laboratorio;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AulaRepository extends JpaRepository<BeanAula, Long> {
}
