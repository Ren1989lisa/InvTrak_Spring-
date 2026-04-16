package com.example.integradora5d.controllers.prioridad;

import com.example.integradora5d.models.prioridad.BeanPrioridad;
import com.example.integradora5d.models.prioridad.PrioridadRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prioridad")
public class PrioridadController {

    private final PrioridadRepository prioridadRepository;

    public PrioridadController(PrioridadRepository prioridadRepository) {
        this.prioridadRepository = prioridadRepository;
    }

    @GetMapping
    public ResponseEntity<List<BeanPrioridad>> getAll() {
        return ResponseEntity.ok(prioridadRepository.findAll());
    }
}
