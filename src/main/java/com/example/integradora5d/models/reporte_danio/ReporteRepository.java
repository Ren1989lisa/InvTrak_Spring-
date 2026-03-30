package com.example.integradora5d.models.reporte_danio;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<BeanReporte, Long> {
}
