package com.example.integradora5d.service.reporte_danio;

import com.example.integradora5d.dto.reporte_danio.CreateReporteDTO;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import com.example.integradora5d.models.evidencia.BeanEvidencia;
import com.example.integradora5d.models.evidencia.ENUM_TIPO_ARCHIVO;
import com.example.integradora5d.models.evidencia.ENUM_TIPO_EVIDENCIA;
import com.example.integradora5d.models.evidencia.EvidenciaRepository;
import com.example.integradora5d.models.prioridad.BeanPrioridad;
import com.example.integradora5d.models.prioridad.PrioridadRepository;
import com.example.integradora5d.models.reporte_danio.BeanReporte;
import com.example.integradora5d.models.reporte_danio.ENUM_REPORTEDANIO;
import com.example.integradora5d.models.reporte_danio.ReporteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Transactional(readOnly = true)
    public BeanReporte getById(Long reporteId) {
        return reporteRepository.findById(reporteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"));
    }

    @Transactional(rollbackFor = Exception.class)
    public BeanReporte create(CreateReporteDTO dto, List<MultipartFile> archivos) throws IOException {
        if (archivos == null || archivos.isEmpty() || archivos.get(0).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Es obligatorio subir al menos una evidencia fotografica."
            );
        }

        BeanActivo activo = activoRepository.findById(dto.getActivoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Activo con ID " + dto.getActivoId() + " no encontrado"
                ));

        BeanPrioridad prioridad = resolverPrioridad(dto.getPrioridadId());

        activo.setEstatus(ENUM_ESTATUS_ACTIVO.REPORTADO);
        activoRepository.save(activo);

        BeanReporte reporte = new BeanReporte();
        reporte.setActivo(activo);
        reporte.setTipo_falla(dto.getTipoFalla());
        reporte.setDescripcion(dto.getDescripcion());
        reporte.setPrioridad(prioridad);
        reporte.setFecha_reporte(LocalDate.now());
        reporte.setEstatus(ENUM_REPORTEDANIO.ABIERTO);

        BeanReporte reporteGuardado = reporteRepository.save(reporte);

        Path rootPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        if (!Files.exists(rootPath)) {
            Files.createDirectories(rootPath);
        }

        for (MultipartFile archivo : archivos) {
            if (archivo.isEmpty()) {
                continue;
            }

            String nombreOriginal = archivo.getOriginalFilename() != null
                    ? archivo.getOriginalFilename().replaceAll("\\s", "_")
                    : "foto.jpg";
            String nombreFinal = UUID.randomUUID() + "_" + nombreOriginal;

            Path rutaDestino = rootPath.resolve(nombreFinal);
            Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            BeanEvidencia evidencia = new BeanEvidencia();
            evidencia.setRuta_archivo(nombreFinal);
            evidencia.setFecha_subida(LocalDateTime.now());
            evidencia.setTipo(ENUM_TIPO_EVIDENCIA.REPORTE);

            String contentType = archivo.getContentType();
            evidencia.setTipo_archivo(contentType != null && contentType.startsWith("video")
                    ? ENUM_TIPO_ARCHIVO.VIDEO
                    : ENUM_TIPO_ARCHIVO.IMAGEN);

            evidencia.setReporte(reporteGuardado);
            evidenciaRepository.save(evidencia);
        }

        return reporteGuardado;
    }

    private BeanPrioridad resolverPrioridad(Long prioridadId) {
        if (prioridadId != null) {
            return prioridadRepository.findById(prioridadId)
                    .orElseGet(() -> prioridadRepository.findFirstPrioridad()
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "No hay prioridades registradas para crear el reporte"
                            )));
        }

        return prioridadRepository.findFirstPrioridad()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No hay prioridades registradas para crear el reporte"
                ));
    }
}
