package com.example.integradora5d.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActiveStatusEnumSync {

    private final JdbcTemplate jdbcTemplate;

    public ActiveStatusEnumSync(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void syncActivoStatusEnum() {
        jdbcTemplate.execute("""
                ALTER TABLE activo
                MODIFY COLUMN estatus ENUM(
                    'DISPONIBLE',
                    'RESGUARDADO',
                    'MANTENIMIENTO',
                    'BAJA',
                    'REPORTADO',
                    'DEVOLUCION',
                    'SOLICITUD_DE_BAJA'
                )
                """);
    }
}
