package com.example.integradora5d.models.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<BeanUsuario, Long> {

    Optional<BeanUsuario> findByCorreo(String correo);
}
