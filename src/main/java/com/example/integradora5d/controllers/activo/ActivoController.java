package com.example.integradora5d.controllers.activo;

import com.example.integradora5d.dto.activo.CreateActivoDTO;
import com.example.integradora5d.dto.activo.UpdateActivoDTO;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.service.activo.ActivoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    /** Ruta literal antes de /{id} para que no se interprete como id numérico. */
    @GetMapping("/disponibles")
    public ResponseEntity<List<BeanActivo>> getDisponibles() {
        return ResponseEntity.ok(activoService.getDisponibles());
    }

    /** Móvil: activos filtrados según el rol del usuario autenticado. */
    @GetMapping("/mis-activos")
    public ResponseEntity<List<BeanActivo>> getMisActivos(Principal principal) {
        return ResponseEntity.ok(activoService.getMisActivos(principal.getName()));
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
                                             @Valid @RequestBody UpdateActivoDTO dto) {
        return ResponseEntity.ok(activoService.update(id, dto));
    }
}
