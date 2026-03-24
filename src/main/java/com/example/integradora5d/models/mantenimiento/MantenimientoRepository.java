package com.example.integradora5d.models.mantenimiento;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MantenimientoRepository extends JpaRepository<BeanMantenimiento, Long> {
}
