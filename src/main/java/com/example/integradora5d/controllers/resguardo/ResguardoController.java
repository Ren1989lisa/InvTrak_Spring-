package com.example.integradora5d.controllers.resguardo;

import com.example.integradora5d.dto.resguardo.ConfirmarResguardoDTO;
import com.example.integradora5d.dto.resguardo.CreateResguardoDTO;
import com.example.integradora5d.dto.resguardo.DevolucionDTO;
import com.example.integradora5d.models.resguardo.BeanResguardo;
import com.example.integradora5d.service.resguardo.ResguardoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resguardo")
public class ResguardoController {

    private final ResguardoService resguardoService;

    public ResguardoController(ResguardoService resguardoService) {
        this.resguardoService = resguardoService;
    }

    // WEB - Admin asigna activo
    @PostMapping
    public ResponseEntity<BeanResguardo> asignar(@Valid @RequestBody CreateResguardoDTO dto) {
        return new ResponseEntity<>(resguardoService.asignar(dto), HttpStatus.CREATED);
    }

    // MÓVIL - Verificar QR (devuelve el resguardo pendiente)
    @GetMapping("/verificar/{activoId}")
    public ResponseEntity<BeanResguardo> verificarQR(@PathVariable Long activoId) {
        return ResponseEntity.ok(resguardoService.verificarQR(activoId));
    }

    // MÓVIL - Confirmar resguardo con checklist y fotos
    @PostMapping(value = "/confirmar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BeanResguardo> confirmar(
            @RequestPart("datos") @Valid ConfirmarResguardoDTO dto,
            @RequestPart(value = "fotos", required = false) List<MultipartFile> fotos) throws Exception {
        return ResponseEntity.ok(resguardoService.confirmar(dto, fotos));
    }

    // MÓVIL - Devolución con fotos obligatorias
    @PostMapping(value = "/devolver", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BeanResguardo> devolver(
            @RequestPart("datos") @Valid DevolucionDTO dto,
            @RequestPart("fotos") List<MultipartFile> fotos) throws Exception {
        return ResponseEntity.ok(resguardoService.devolver(dto, fotos));
    }
}
