package com.example.integradora5d.controllers.reporte_danio;

import com.example.integradora5d.dto.reporte_danio.CreateReporteDTO;
import com.example.integradora5d.models.reporte_danio.BeanReporte;
import com.example.integradora5d.service.reporte_danio.ReporteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reporte")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public ResponseEntity<List<BeanReporte>> getAll() {
        return ResponseEntity.ok(reporteService.getAll());
    }

    @GetMapping("/activo/{activoId}")
    public ResponseEntity<List<BeanReporte>> getByActivo(@PathVariable Long activoId) {
        return ResponseEntity.ok(reporteService.getByActivo(activoId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BeanReporte> create(
            @RequestPart("datos") @Valid CreateReporteDTO dto,
            @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos)
            throws Exception {
        return new ResponseEntity<>(reporteService.create(dto, archivos), HttpStatus.CREATED);
    }
}