package com.example.integradora5d.controllers.activo;

import com.example.integradora5d.dto.activo.ActivoResponseDTO;
import com.example.integradora5d.dto.activo.CreateActivoDTO;
import com.example.integradora5d.dto.activo.UpdateActivoDTO;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.service.activo.ActivoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activo")
public class ActivoController {

    private final ActivoService activoService;

    public ActivoController(ActivoService activoService) {
        this.activoService = activoService;
    }

    @GetMapping
    public ResponseEntity<List<ActivoResponseDTO>> getAll() {
        return ResponseEntity.ok(
            activoService.getAll().stream().map(ActivoResponseDTO::from).toList()
        );
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ActivoResponseDTO>> getDisponibles() {
        return ResponseEntity.ok(
            activoService.getDisponibles().stream().map(ActivoResponseDTO::from).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ActivoResponseDTO.from(activoService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ActivoResponseDTO> create(@Valid @RequestBody CreateActivoDTO dto) {
        return new ResponseEntity<>(ActivoResponseDTO.from(activoService.create(dto)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivoResponseDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody UpdateActivoDTO dto) {
        return ResponseEntity.ok(ActivoResponseDTO.from(activoService.update(id, dto)));
    }
}
