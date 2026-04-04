package com.example.integradora5d.service.dashboard;

import com.example.integradora5d.dto.dashboard.ActivoReportadoDTO;
import com.example.integradora5d.dto.dashboard.DashboardDTO;
import com.example.integradora5d.dto.dashboard.MantenimientoTecnicoDTO;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import com.example.integradora5d.models.mantenimiento.MantenimientoRepository;
import com.example.integradora5d.models.reporte_danio.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ActivoRepository activoRepository;
    private final ReporteRepository reporteRepository;
    private final MantenimientoRepository mantenimientoRepository;

    public DashboardService(ActivoRepository activoRepository,
                            ReporteRepository reporteRepository,
                            MantenimientoRepository mantenimientoRepository) {
        this.activoRepository = activoRepository;
        this.reporteRepository = reporteRepository;
        this.mantenimientoRepository = mantenimientoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDTO getDashboard() {

        // 1. Total de activos
        long totalActivos = activoRepository.count();

        // 2. Activos por estatus
        Map<String, Long> activosPorEstatus = new LinkedHashMap<>();
        for (ENUM_ESTATUS_ACTIVO estatus : ENUM_ESTATUS_ACTIVO.values()) {
            long count = activoRepository.countByEstatus(estatus);
            activosPorEstatus.put(estatus.name(), count);
        }

        // 3. Activos más reportados
        List<ActivoReportadoDTO> activosMasReportados = reporteRepository
                .findActivosMasReportados()
                .stream()
                .limit(5)
                .map(row -> new ActivoReportadoDTO(
                        (Long) row[0],
                        activoRepository.findById((Long) row[0])
                                .map(a -> a.getEtiquetaBien())
                                .orElse("Desconocido"),
                        (Long) row[1]
                ))
                .toList();

        // 4. Mantenimientos por técnico
        List<MantenimientoTecnicoDTO> mantenimientosPorTecnico = mantenimientoRepository
                .countByTecnico()
                .stream()
                .map(row -> new MantenimientoTecnicoDTO(
                        (String) row[0],
                        (Long) row[1]
                ))
                .toList();

        // 5. Tiempo promedio de atención en días
        Double tiempoPromedio = mantenimientoRepository.promedioTiempoAtencion();

        return new DashboardDTO(
                totalActivos,
                activosPorEstatus,
                activosMasReportados,
                mantenimientosPorTecnico,
                tiempoPromedio
        );
    }
}