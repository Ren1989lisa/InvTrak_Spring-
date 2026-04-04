package com.example.integradora5d.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class DashboardDTO {
    private long totalActivos;
    private Map<String, Long> activosPorEstatus;
    private List<ActivoReportadoDTO> activosMasReportados;
    private List<MantenimientoTecnicoDTO> mantenimientosPorTecnico;
    private Double tiempoPromedioAtencion;
}