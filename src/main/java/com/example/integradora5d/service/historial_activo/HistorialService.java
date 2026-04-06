package com.example.integradora5d.service.historial_activo;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.historial_activo.BeanHistorial;
import com.example.integradora5d.models.historial_activo.HistorialRepository;
import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistorialService {

    private final HistorialRepository historialRepository;

    public HistorialService(HistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    // Llamado internamente cada vez que cambia el estatus de un activo
    @Transactional
    public void registrar(BeanActivo activo,
                          String estatusAnterior,
                          String estatusNuevo,
                          String motivo,
                          BeanUsuario usuario) {
        BeanHistorial historial = new BeanHistorial();
        historial.setActivo(activo);
        historial.setEstatus_anterior(estatusAnterior);
        historial.setEstatus_nuevo(estatusNuevo);
        historial.setFecha_cambio(LocalDateTime.now());
        historial.setMotivo(motivo);
        historial.setUsuario(usuario);
        historialRepository.save(historial);
    }

    @Transactional(readOnly = true)
    public List<BeanHistorial> getAll() {
        return historialRepository.findAllOrdenado();
    }

    @Transactional(readOnly = true)
    public List<BeanHistorial> getByActivo(Long activoId) {
        return historialRepository.findByActivoId(activoId);
    }
}