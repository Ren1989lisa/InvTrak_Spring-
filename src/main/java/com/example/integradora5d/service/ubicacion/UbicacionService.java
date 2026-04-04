package com.example.integradora5d.service.ubicacion;

import com.example.integradora5d.dto.ubicacion.CreateUbicacionDTO;
import com.example.integradora5d.dto.ubicacion.UpdateUbicacionDTO;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.aula_laboratorio.AulaRepository;
import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.campus.BeanCampus;
import com.example.integradora5d.models.campus.CampusRepository;
import com.example.integradora5d.models.edificio.BeanEdificio;
import com.example.integradora5d.models.edificio.EdificioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UbicacionService {

    private final CampusRepository campusRepository;
    private final EdificioRepository edificioRepository;
    private final AulaRepository aulaRepository;
    private final ActivoRepository activoRepository;

    public UbicacionService(CampusRepository campusRepository,
                            EdificioRepository edificioRepository,
                            AulaRepository aulaRepository,
                            ActivoRepository activoRepository) {
        this.campusRepository = campusRepository;
        this.edificioRepository = edificioRepository;
        this.aulaRepository = aulaRepository;
        this.activoRepository = activoRepository;
    }

    // --- GETS para dropdowns ---
    public List<BeanCampus> getAllCampus() {
        return campusRepository.findAll();
    }

    public List<BeanEdificio> getEdificiosByCampus(Long campusId) {
        return edificioRepository.findByCampus_IdCampus(campusId);
    }

    public List<BeanAula> getAulasByEdificio(Long edificioId) {
        return aulaRepository.findByEdificio_IdEdificio(edificioId);
    }

    public List<BeanAula> getAllAulas() {
        return aulaRepository.findAll();
    }

    // --- CREAR ---
    @Transactional
    public BeanAula create(CreateUbicacionDTO dto) {

        // 1. Resolver campus
        BeanCampus campus;
        if (dto.getCampusId() != null) {
            campus = campusRepository.findById(dto.getCampusId())
                    .orElseThrow(() -> new RuntimeException("Campus no encontrado"));
        } else if (dto.getCampusNombre() != null && !dto.getCampusNombre().isBlank()) {
            campus = campusRepository.findByNombre(dto.getCampusNombre())
                    .orElseGet(() -> {
                        BeanCampus nuevo = new BeanCampus();
                        nuevo.setNombre(dto.getCampusNombre());
                        return campusRepository.save(nuevo);
                    });
        } else {
            throw new RuntimeException("Debes proporcionar un campus");
        }

        // 2. Resolver edificio
        BeanEdificio edificio;
        if (dto.getEdificioId() != null) {
            edificio = edificioRepository.findById(dto.getEdificioId())
                    .orElseThrow(() -> new RuntimeException("Edificio no encontrado"));
        } else if (dto.getEdificioNombre() != null && !dto.getEdificioNombre().isBlank()) {
            BeanCampus finalCampus = campus;
            edificio = edificioRepository
                    .findByNombreAndCampus_IdCampus(dto.getEdificioNombre(), campus.getIdCampus())
                    .orElseGet(() -> {
                        BeanEdificio nuevo = new BeanEdificio();
                        nuevo.setNombre(dto.getEdificioNombre());
                        nuevo.setCampus(finalCampus);
                        return edificioRepository.save(nuevo);
                    });
        } else {
            throw new RuntimeException("Debes proporcionar un edificio");
        }

        // 3. Crear aula
        BeanEdificio finalEdificio = edificio;
        BeanAula aula = aulaRepository
                .findByNombreAndEdificio_IdEdificio(dto.getAulaNombre(), edificio.getIdEdificio())
                .orElseGet(() -> {
                    BeanAula nueva = new BeanAula();
                    nueva.setNombre(dto.getAulaNombre());
                    nueva.setDescripcion(dto.getDescripcion());
                    nueva.setEdificio(finalEdificio);
                    return aulaRepository.save(nueva);
                });

        return aula;
    }

    // --- EDITAR (solo descripción) ---
    @Transactional
    public BeanAula update(Long id, UpdateUbicacionDTO dto) {
        BeanAula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        if (dto.getDescripcion() != null) {
            aula.setDescripcion(dto.getDescripcion());
        }

        return aulaRepository.save(aula);
    }

    // --- ELIMINAR ---
    @Transactional
    public void delete(Long id) {
        BeanAula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        if (activoRepository.existsByAula(aula)) {
            throw new RuntimeException("No se puede eliminar, el aula tiene activos vinculados");
        }

        aulaRepository.delete(aula);
    }
}
