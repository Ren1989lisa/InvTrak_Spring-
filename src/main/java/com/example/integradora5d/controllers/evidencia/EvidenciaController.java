package com.example.integradora5d.controllers.evidencia;

import com.example.integradora5d.dto.evidencia.EvidenciaResponseDTO;
import com.example.integradora5d.service.evidencia.EvidenciaService;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/evidencia")
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    public EvidenciaController(EvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvidenciaResponseDTO> getById(@PathVariable("id") Long evidenciaId) {
        return ResponseEntity.ok(evidenciaService.getById(evidenciaId));
    }

    @GetMapping("/reporte/{reporteId}")
    public ResponseEntity<List<EvidenciaResponseDTO>> getByReporteId(@PathVariable Long reporteId) {
        return ResponseEntity.ok(evidenciaService.getByReporteId(reporteId));
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<?> getArchivo(@PathVariable("id") Long evidenciaId) {
        EvidenciaService.ArchivoEvidencia archivo = evidenciaService.getArchivoById(evidenciaId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofHours(12)).cachePublic())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(archivo.fileName()).build().toString()
                )
                .contentType(archivo.mediaType())
                .body(archivo.resource());
    }
}
