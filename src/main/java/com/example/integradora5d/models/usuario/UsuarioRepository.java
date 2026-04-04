package com.example.integradora5d.models.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<BeanUsuario, Long> {

    Optional<BeanUsuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
    boolean existsByCurp(String curp);

    long countByRolId(Long rolId);

    List<BeanUsuario> findByRol_Nombre(String rolNombre);

    // Para verificar si tiene resguardos activos
    boolean existsByResguardos_Activo_IdActivoIsNotNull(Long usuarioId);

    @Query("SELECT COUNT(r) > 0 FROM BeanResguardo r WHERE r.usuario.idUsuario = :usuarioId")
    boolean tieneResguardos(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(m) > 0 FROM BeanMantenimiento m WHERE m.tecnico.idUsuario = :usuarioId")
    boolean tieneMantenimientos(@Param("usuarioId") Long usuarioId);
}
