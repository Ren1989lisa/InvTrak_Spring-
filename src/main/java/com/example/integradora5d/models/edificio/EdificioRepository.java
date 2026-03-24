package com.example.integradora5d.models.edificio;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdificioRepository extends JpaRepository<BeanEdificio, Long> {
}
