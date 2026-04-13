package com.example.integradora5d.dto.resguardo;

import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import lombok.Getter;
import lombok.Setter;

/**
 * Actualización parcial (PATCH semántico) vía PUT {@code /api/resguardo/{id}}.
 * <p>
 * JSON de entrada (camelCase, todos opcionales salvo que al menos uno deba enviarse en la práctica):
 * <pre>
 * {
 *   "confirmado": true,
 *   "observaciones": "texto opcional",
 *   "estado": "RESGUARDADO"
 * }
 * </pre>
 * {@code estado} corresponde al {@link ENUM_ESTATUS_ACTIVO} del activo vinculado y solo puede
 * establecerlo un usuario con rol ADMINISTRADOR.
 */
@Getter
@Setter
public class ResguardoUpdateDTO {

    /** Si no es null, se aplica el nuevo valor de confirmación. */
    private Boolean confirmado;

    /** Si no es null, se sustituyen las observaciones del resguardo. */
    private String observaciones;

    /**
     * Estatus del activo asociado (mismo enum que en dominio). Solo administradores.
     */
    private ENUM_ESTATUS_ACTIVO estado;
}
