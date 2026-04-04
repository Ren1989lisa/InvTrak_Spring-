package com.example.integradora5d.controllers.activo;

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
    public ResponseEntity<List<BeanActivo>> getAll() {
        return ResponseEntity.ok(activoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeanActivo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(activoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BeanActivo> create(@Valid @RequestBody CreateActivoDTO dto) {
        return new ResponseEntity<>(activoService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeanActivo> update(@PathVariable Long id,
                                             @RequestBody UpdateActivoDTO dto) {
        return ResponseEntity.ok(activoService.update(id, dto));
    }
}
