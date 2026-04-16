package com.example.integradora5d.service.reporte_danio;

import com.example.integradora5d.dto.reporte_danio.CreateReporteDTO;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import com.example.integradora5d.models.evidencia.*;
import com.example.integradora5d.models.prioridad.BeanPrioridad;
import com.example.integradora5d.models.prioridad.PrioridadRepository;
import com.example.integradora5d.models.reporte_danio.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
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

    // Directorio de subida (Ruta relativa a la raíz del proyecto)
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

    @Transactional(rollbackFor = Exception.class)
    public BeanReporte create(CreateReporteDTO dto, List<MultipartFile> archivos) throws IOException {

        // 1. VALIDACIÓN: Obligatorio al menos una imagen
        if (archivos == null || archivos.isEmpty() || archivos.get(0).isEmpty()) {
            throw new RuntimeException("Es obligatorio subir al menos una evidencia fotográfica.");
        }

        // 2. Verificar existencia de dependencias
        BeanActivo activo = activoRepository.findById(dto.getActivoId())
                .orElseThrow(() -> new RuntimeException(
                        "Activo con ID " + dto.getActivoId() + " no encontrado"
                ));
        /* CAMBIAR ESTATUS DEL ACTIVO */
        activo.setEstatus(ENUM_ESTATUS_ACTIVO.REPORTADO);
        activoRepository.save(activo);

        BeanPrioridad prioridad = prioridadRepository.findById(dto.getPrioridadId())
                .orElseThrow(() -> new RuntimeException("Prioridad con ID " + dto.getPrioridadId() + " no encontrada"));

        // 3. Crear el reporte
        BeanReporte reporte = new BeanReporte();
        reporte.setActivo(activo);
        reporte.setTipo_falla(dto.getTipoFalla());
        reporte.setDescripcion(dto.getDescripcion());
        reporte.setPrioridad(prioridad);
        reporte.setFecha_reporte(LocalDate.now());
        reporte.setEstatus(ENUM_REPORTEDANIO.ABIERTO);

        // Guardamos primero el reporte para tener el ID para las evidencias
        BeanReporte reporteGuardado = reporteRepository.save(reporte);

        // 4. Manejo de Archivos
        Path rootPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        if (!Files.exists(rootPath)) {
            Files.createDirectories(rootPath);
        }

        for (MultipartFile archivo : archivos) {
            if (archivo.isEmpty()) continue;

            // Generar nombre único y limpiar el original
            String nombreOriginal = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename().replaceAll("\\s", "_") : "foto.jpg";
            String nombreFinal = UUID.randomUUID().toString() + "_" + nombreOriginal;

            Path rutaDestino = rootPath.resolve(nombreFinal);
            Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            // 5. Crear registro de Evidencia
            BeanEvidencia evidencia = new BeanEvidencia();
            evidencia.setRuta_archivo(nombreFinal); // Guardamos solo el nombre para que sea portátil
            evidencia.setFecha_subida(LocalDateTime.now());
            evidencia.setTipo(ENUM_TIPO_EVIDENCIA.REPORTE);

            // Determinar si es video o imagen
            String contentType = archivo.getContentType();
            evidencia.setTipo_archivo(contentType != null && contentType.startsWith("video")
                    ? ENUM_TIPO_ARCHIVO.VIDEO : ENUM_TIPO_ARCHIVO.IMAGEN);

            evidencia.setReporte(reporteGuardado);
            evidenciaRepository.save(evidencia);
        }

        return reporteGuardado;
    }
}