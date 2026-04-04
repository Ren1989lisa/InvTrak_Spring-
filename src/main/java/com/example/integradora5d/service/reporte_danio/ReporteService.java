package com.example.integradora5d.service.reporte_danio;

import com.example.integradora5d.dto.reporte_danio.CreateReporteDTO;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.evidencia.BeanEvidencia;
import com.example.integradora5d.models.evidencia.ENUM_TIPO_ARCHIVO;
import com.example.integradora5d.models.evidencia.ENUM_TIPO_EVIDENCIA;
import com.example.integradora5d.models.evidencia.EvidenciaRepository;
import com.example.integradora5d.models.prioridad.BeanPrioridad;
import com.example.integradora5d.models.prioridad.PrioridadRepository;
import com.example.integradora5d.models.reporte_danio.BeanReporte;
import com.example.integradora5d.models.reporte_danio.ENUM_REPORTEDANIO;
import com.example.integradora5d.models.reporte_danio.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final ActivoRepository activoRepository;
    private final PrioridadRepository prioridadRepository;
    private final EvidenciaRepository evidenciaRepository;

    private static final String UPLOAD_DIR = "uploads/reportes/";

    public ReporteService(ReporteRepository reporteRepository,
                          ActivoRepository activoRepository,
                          PrioridadRepository prioridadRepository,
                          EvidenciaRepository evidenciaRepository) {
        this.reporteRepository = reporteRepository;
        this.activoRepository = activoRepository;
        this.prioridadRepository = prioridadRepository;
        this.evidenciaRepository = evidenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<BeanReporte> getAll() {
        return reporteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BeanReporte> getByActivo(Long activoId) {
        return reporteRepository.findByActivo_IdActivo(activoId);
    }

    @Transactional
    public BeanReporte create(CreateReporteDTO dto,
                              List<MultipartFile> archivos) throws IOException {

        BeanActivo activo = activoRepository.findById(dto.getActivoId())
                .orElseThrow(() -> new RuntimeException("Activo no encontrado"));

        BeanPrioridad prioridad = prioridadRepository.findById(dto.getPrioridadId())
                .orElseThrow(() -> new RuntimeException("Prioridad no encontrada"));

        BeanReporte reporte = new BeanReporte();
        reporte.setActivo(activo);
        reporte.setTipo_falla(dto.getTipoFalla());
        reporte.setDescripcion(dto.getDescripcion());
        reporte.setPrioridad(prioridad);
        reporte.setFecha_reporte(LocalDate.now());
        reporte.setEstatus(ENUM_REPORTEDANIO.ABIERTO);

        reporteRepository.save(reporte);

        // Guardar fotos y videos
        if (archivos != null && !archivos.isEmpty()) {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            for (MultipartFile archivo : archivos) {
                String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
                Path ruta = Paths.get(UPLOAD_DIR + nombreArchivo);
                Files.write(ruta, archivo.getBytes());

                // Detectar si es video o imagen por el content type
                String contentType = archivo.getContentType();
                ENUM_TIPO_ARCHIVO tipoArchivo = (contentType != null && contentType.startsWith("video"))
                        ? ENUM_TIPO_ARCHIVO.VIDEO
                        : ENUM_TIPO_ARCHIVO.IMAGEN;

                BeanEvidencia evidencia = new BeanEvidencia();
                evidencia.setRuta_archivo(ruta.toString());
                evidencia.setFecha_subida(LocalDateTime.now());
                evidencia.setTipo(ENUM_TIPO_EVIDENCIA.REPORTE);
                evidencia.setTipo_archivo(tipoArchivo);
                evidencia.setReporte(reporte);
                evidenciaRepository.save(evidencia);
            }
        }

        return reporte;
    }
}