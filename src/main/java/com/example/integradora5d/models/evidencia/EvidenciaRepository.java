package com.example.integradora5d.models.evidencia;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvidenciaRepository extends JpaRepository<BeanEvidencia, Long> {
}
