package com.example.integradora5d.service.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.BeanActivo;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRService {

    private final ActivoRepository activoRepository;

    public QRService(ActivoRepository activoRepository) {
        this.activoRepository = activoRepository;
    }

    public byte[] generarQR(Long activoId) throws WriterException, IOException {

        BeanActivo activo = activoRepository.findById(activoId)
                .orElseThrow(() -> new RuntimeException("Activo no encontrado"));

        // Contenido del QR: ID y etiqueta del bien
        String contenido = "ID:" + activo.getIdActivo() + "|ETIQUETA:" + activo.getEtiquetaBien();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }
}