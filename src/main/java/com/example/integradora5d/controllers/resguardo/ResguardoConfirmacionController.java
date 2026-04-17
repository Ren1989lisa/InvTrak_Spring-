package com.example.integradora5d.controllers.resguardo;

import com.example.integradora5d.dto.resguardo.ConfirmarResguardoQrDTO;
import com.example.integradora5d.service.resguardo.ResguardoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/resguardos")
public class ResguardoConfirmacionController {

    private final ResguardoService resguardoService;

    public ResguardoConfirmacionController(ResguardoService resguardoService) {
        this.resguardoService = resguardoService;
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, String>> confirmar(@Valid @RequestBody ConfirmarResguardoQrDTO request) {
        return ResponseEntity.ok(resguardoService.confirmarPorQr(request.getIdActivo()));
    }
}
