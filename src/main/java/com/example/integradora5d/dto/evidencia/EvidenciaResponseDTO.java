package com.example.integradora5d.dto.evidencia;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EvidenciaResponseDTO {
    private Long idEvidencia;
    private String rutaArchivo;
    private String tipo;
    private String tipoArchivo;
    private LocalDateTime fechaSubida;
    private Long reporteId;
    private Long mantenimientoId;
    private Long resguardoId;
    private String urlArchivo;
}
