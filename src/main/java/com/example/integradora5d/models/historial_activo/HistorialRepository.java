package com.example.integradora5d.models.historial_activo;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialRepository extends JpaRepository<BeanHistorial, Long> {
}
