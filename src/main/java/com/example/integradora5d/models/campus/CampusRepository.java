package com.example.integradora5d.models.campus;

import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampusRepository extends JpaRepository<BeanCampus, Long> {
}
