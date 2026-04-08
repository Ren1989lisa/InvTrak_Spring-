package com.example.integradora5d.controllers.qr;

import com.example.integradora5d.service.qr.QRService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
public class QRController {

    private final QRService qrService;

    public QRController(QRService qrService) {
        this.qrService = qrService;
    }

    // Devuelve el QR como imagen PNG
    @GetMapping("/{activoId}")
    public ResponseEntity<byte[]> getQR(@PathVariable Long activoId) throws Exception {
        byte[] qr = qrService.generarQR(activoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qr-" + activoId + ".png")
                .contentType(MediaType.IMAGE_PNG)
                .body(qr);
    }

    // Devuelve el QR para descargar
    @GetMapping("/{activoId}/descargar")
    public ResponseEntity<byte[]> descargarQR(@PathVariable Long activoId) throws Exception {
        byte[] qr = qrService.generarQR(activoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr-" + activoId + ".png")
                .contentType(MediaType.IMAGE_PNG)
                .body(qr);
    }
}
