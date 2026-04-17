package com.example.integradora5d.controllers.resguardo;

import com.example.integradora5d.dto.resguardo.ConfirmarResguardoDTO;
import com.example.integradora5d.dto.resguardo.CreateResguardoDTO;
import com.example.integradora5d.dto.resguardo.DevolucionDTO;
import com.example.integradora5d.dto.resguardo.ResguardoResponseDTO;
import com.example.integradora5d.dto.resguardo.ResguardoUpdateDTO;
import com.example.integradora5d.service.resguardo.ResguardoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
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
    public ResponseEntity<ResguardoResponseDTO> asignar(@Valid @RequestBody CreateResguardoDTO dto) {
        return new ResponseEntity<>(resguardoService.asignar(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ResguardoResponseDTO>> listar(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return ResponseEntity.ok(resguardoService.listar(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResguardoResponseDTO> obtenerPorId(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return ResponseEntity.ok(resguardoService.obtenerPorId(id, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResguardoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ResguardoUpdateDTO dto,
            Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return ResponseEntity.ok(resguardoService.actualizar(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        resguardoService.eliminar(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // MOVIL - Solicitar devolución (cambia estatus activo a DEVOLUCION)
    @PostMapping("/{id}/solicitar-devolucion")
    public ResponseEntity<ResguardoResponseDTO> solicitarDevolucion(
            @PathVariable Long id,
            Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return ResponseEntity.ok(resguardoService.solicitarDevolucion(id, principal.getName()));
    }

    @PutMapping("/{id}/cancelar-baja")
    public ResponseEntity<ResguardoResponseDTO> cancelarBaja(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return ResponseEntity.ok(resguardoService.cancelarBaja(id, principal.getName()));
    }

    // MOVIL - Verificar QR (devuelve el resguardo pendiente)
    @GetMapping("/verificar/{activoId}")
    public ResponseEntity<ResguardoResponseDTO> verificarQR(@PathVariable Long activoId, Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return ResponseEntity.ok(resguardoService.verificarQR(activoId, principal.getName()));
    }

    // MOVIL - Confirmar resguardo con checklist y fotos
    @PostMapping(value = "/confirmar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResguardoResponseDTO> confirmar(
            @RequestPart("datos") @Valid ConfirmarResguardoDTO dto,
            @RequestPart(value = "fotos", required = false) List<MultipartFile> fotos,
            Principal principal) throws Exception {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return ResponseEntity.ok(resguardoService.confirmar(dto, fotos, principal.getName()));
    }

    // MOVIL - Devolucion con fotos obligatorias
    @PostMapping(value = "/devolver", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResguardoResponseDTO> devolver(
            @RequestPart("datos") @Valid DevolucionDTO dto,
            @RequestPart("fotos") List<MultipartFile> fotos,
            Principal principal) throws Exception {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return ResponseEntity.ok(resguardoService.devolver(dto, fotos, principal.getName()));
    }
}
