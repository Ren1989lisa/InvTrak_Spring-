package com.example.integradora5d.controllers.historial_activo;


import com.example.integradora5d.models.historial_activo.BeanHistorial;
import com.example.integradora5d.service.historial_activo.HistorialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    private final HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    public ResponseEntity<List<BeanHistorial>> getAll() {
        return ResponseEntity.ok(historialService.getAll());
    }

    @GetMapping("/activo/{activoId}")
    public ResponseEntity<List<BeanHistorial>> getByActivo(@PathVariable Long activoId) {
        return ResponseEntity.ok(historialService.getByActivo(activoId));
    }
}
