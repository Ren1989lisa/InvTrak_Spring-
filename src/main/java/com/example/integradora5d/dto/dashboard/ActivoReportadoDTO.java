package com.example.integradora5d.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActivoReportadoDTO {
    private Long activoId;
    private String etiquetaBien;
    private Long totalReportes;
}