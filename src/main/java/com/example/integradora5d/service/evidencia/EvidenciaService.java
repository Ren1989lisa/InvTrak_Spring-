package com.example.integradora5d.service.evidencia;

import com.example.integradora5d.dto.evidencia.EvidenciaResponseDTO;
import com.example.integradora5d.models.evidencia.BeanEvidencia;
import com.example.integradora5d.models.evidencia.ENUM_TIPO_ARCHIVO;
import com.example.integradora5d.models.evidencia.EvidenciaRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;

    public EvidenciaService(EvidenciaRepository evidenciaRepository) {
        this.evidenciaRepository = evidenciaRepository;
    }

    @Transactional(readOnly = true)
    public EvidenciaResponseDTO getById(Long evidenciaId) {
        BeanEvidencia evidencia = findEntityById(evidenciaId);
        return toDto(evidencia);
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponseDTO> getByReporteId(Long reporteId) {
        return evidenciaRepository.findByReporteId(reporteId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArchivoEvidencia getArchivoById(Long evidenciaId) {
        BeanEvidencia evidencia = findEntityById(evidenciaId);
        Path filePath = resolveFilePath(evidencia.getRuta_archivo());

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(NOT_FOUND, "Archivo de evidencia no encontrado");
            }

            MediaType mediaType = resolveMediaType(filePath, evidencia.getTipo_archivo());
            return new ArchivoEvidencia(resource, mediaType, filePath.getFileName().toString());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(NOT_FOUND, "Archivo de evidencia no encontrado");
        }
    }

    private BeanEvidencia findEntityById(Long evidenciaId) {
        return evidenciaRepository.findById(evidenciaId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Evidencia no encontrada"));
    }

    private EvidenciaResponseDTO toDto(BeanEvidencia evidencia) {
        EvidenciaResponseDTO dto = new EvidenciaResponseDTO();
        dto.setIdEvidencia(evidencia.getId_foto_resguardo());
        dto.setRutaArchivo(evidencia.getRuta_archivo());
        dto.setFechaSubida(evidencia.getFecha_subida());
        dto.setTipo(evidencia.getTipo() != null ? evidencia.getTipo().name() : null);
        dto.setTipoArchivo(evidencia.getTipo_archivo() != null ? evidencia.getTipo_archivo().name() : null);
        dto.setReporteId(evidencia.getReporte() != null ? evidencia.getReporte().getId_reporte() : null);
        dto.setMantenimientoId(
                evidencia.getMantenimiento() != null ? evidencia.getMantenimiento().getId_mantenimiento() : null
        );
        dto.setResguardoId(evidencia.getResguardo() != null ? evidencia.getResguardo().getIdResguardo() : null);

        String archivoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/evidencia/")
                .path(String.valueOf(evidencia.getId_foto_resguardo()))
                .path("/archivo")
                .toUriString();
        dto.setUrlArchivo(archivoUrl);
        return dto;
    }

    private Path resolveFilePath(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new ResponseStatusException(NOT_FOUND, "Archivo de evidencia no encontrado");
        }

        String cleanPath = rutaArchivo.trim();
        Path rawPath = Paths.get(cleanPath);
        List<Path> candidates = new ArrayList<>();

        if (rawPath.isAbsolute()) {
            candidates.add(rawPath.normalize());
        } else {
            candidates.add(rawPath.toAbsolutePath().normalize());
            candidates.add(Paths.get("uploads").resolve(cleanPath).toAbsolutePath().normalize());
            candidates.add(Paths.get("uploads/reportes").resolve(cleanPath).toAbsolutePath().normalize());
            candidates.add(Paths.get("uploads/evidencias").resolve(cleanPath).toAbsolutePath().normalize());
            candidates.add(Paths.get("uploads/mantenimientos").resolve(cleanPath).toAbsolutePath().normalize());
        }

        for (Path candidate : candidates) {
            if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                return candidate;
            }
        }

        throw new ResponseStatusException(NOT_FOUND, "Archivo de evidencia no encontrado");
    }

    private MediaType resolveMediaType(Path path, ENUM_TIPO_ARCHIVO tipoArchivo) {
        try {
            String probe = Files.probeContentType(path);
            if (probe != null && !probe.isBlank()) {
                return MediaType.parseMediaType(probe);
            }
        } catch (Exception ignored) {
        }

        if (tipoArchivo == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        return switch (tipoArchivo) {
            case PNG -> MediaType.IMAGE_PNG;
            case JPG, IMAGEN -> MediaType.IMAGE_JPEG;
            case PDF -> MediaType.APPLICATION_PDF;
            case VIDEO -> MediaType.valueOf("video/mp4");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    public record ArchivoEvidencia(Resource resource, MediaType mediaType, String fileName) {
    }
}
