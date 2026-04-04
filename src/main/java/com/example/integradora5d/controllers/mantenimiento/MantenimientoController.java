package com.example.integradora5d.controllers.mantenimiento;

import com.example.integradora5d.dto.mantenimiento.AsignarMantenimientoDTO;
import com.example.integradora5d.dto.mantenimiento.AtenderMantenimientoDTO;
import com.example.integradora5d.dto.mantenimiento.CerrarMantenimientoDTO;
import com.example.integradora5d.models.mantenimiento.BeanMantenimiento;
import com.example.integradora5d.models.reporte_danio.BeanReporte;
import com.example.integradora5d.service.mantenimiento.MantenimientoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/mantenimiento")
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    // WEB - Reportes abiertos para asignar
    @GetMapping("/reportes-abiertos")
    public ResponseEntity<List<BeanReporte>> getReportesAbiertos() {
        return ResponseEntity.ok(mantenimientoService.getReportesAbiertos());
    }

    // MÓVIL - Mantenimientos del técnico
    @GetMapping("/tecnico/{tecnicoId}")
    public ResponseEntity<List<BeanMantenimiento>> getByTecnico(@PathVariable Long tecnicoId) {
        return ResponseEntity.ok(mantenimientoService.getByTecnico(tecnicoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeanMantenimiento> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.getById(id));
    }

    // WEB - Admin asigna técnico
    @PostMapping
    public ResponseEntity<BeanMantenimiento> asignar(@Valid @RequestBody AsignarMantenimientoDTO dto) {
        return new ResponseEntity<>(mantenimientoService.asignar(dto), HttpStatus.CREATED);
    }

    // MÓVIL - Técnico atiende con fotos
    @PutMapping(value = "/atender", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BeanMantenimiento> atender(
            @RequestPart("datos") @Valid AtenderMantenimientoDTO dto,
            @RequestPart(value = "fotos", required = false) List<MultipartFile> fotos)
            throws Exception {
        return ResponseEntity.ok(mantenimientoService.atender(dto, fotos));
    }

    // MÓVIL/WEB - Cerrar mantenimiento
    @PutMapping("/cerrar")
    public ResponseEntity<BeanMantenimiento> cerrar(@Valid @RequestBody CerrarMantenimientoDTO dto) {
        return ResponseEntity.ok(mantenimientoService.cerrar(dto));
    }
}