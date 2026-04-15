package com.example.integradora5d.controllers.ubicacion;

import com.example.integradora5d.dto.ubicacion.CreateUbicacionDTO;
import com.example.integradora5d.dto.ubicacion.UpdateUbicacionDTO;
import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.campus.BeanCampus;
import com.example.integradora5d.models.edificio.BeanEdificio;
import com.example.integradora5d.service.ubicacion.UbicacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicacion")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    // Dropdowns encadenados
    @GetMapping("/campus")
    public ResponseEntity<List<BeanCampus>> getCampus() {
        return ResponseEntity.ok(ubicacionService.getAllCampus());
    }

    @GetMapping("/edificio/{campusId}")
    public ResponseEntity<List<BeanEdificio>> getEdificios(@PathVariable Long campusId) {
        return ResponseEntity.ok(ubicacionService.getEdificiosByCampus(campusId));
    }

    @GetMapping("/aula/{edificioId}")
    public ResponseEntity<List<BeanAula>> getAulas(@PathVariable Long edificioId) {
        return ResponseEntity.ok(ubicacionService.getAulasByEdificio(edificioId));
    }

    // CRUD principal (sobre aula)
    @GetMapping
    public ResponseEntity<List<BeanAula>> getAll() {
        return ResponseEntity.ok(ubicacionService.getAllAulas());
    }

    @PostMapping
    public ResponseEntity<BeanAula> create(@Valid @RequestBody CreateUbicacionDTO dto) {
        return new ResponseEntity<>(ubicacionService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeanAula> update(@PathVariable Long id,
                                           @Valid @RequestBody UpdateUbicacionDTO dto) {
        return ResponseEntity.ok(ubicacionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ubicacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}