package com.example.integradora5d.service.activo;

import com.example.integradora5d.dto.activo.CreateActivoDTO;
import com.example.integradora5d.dto.activo.UpdateActivoDTO;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import com.example.integradora5d.models.aula_laboratorio.AulaRepository;
import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.edificio.BeanEdificio;
import com.example.integradora5d.models.campus.BeanCampus;
import com.example.integradora5d.models.producto.BeanProducto;
import com.example.integradora5d.models.producto.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivoService {

    private final ActivoRepository activoRepository;
    private final ProductoRepository productoRepository;
    private final AulaRepository aulaRepository;

    public ActivoService(ActivoRepository activoRepository,
                         ProductoRepository productoRepository,
                         AulaRepository aulaRepository) {
        this.activoRepository = activoRepository;
        this.productoRepository = productoRepository;
        this.aulaRepository = aulaRepository;
    }

    public String generarEtiquetaPublica(BeanProducto producto, BeanAula aula) {
        // Primeras 2 letras del nombre del producto
        String nombreProducto = producto.getNombre().length() >= 2
                ? producto.getNombre().substring(0, 2).toUpperCase()
                : producto.getNombre().toUpperCase();

        // Primeras 2 letras de la marca
        String marca = producto.getModelo().getMarca().getNombre().length() >= 2
                ? producto.getModelo().getMarca().getNombre().substring(0, 2).toUpperCase()
                : producto.getModelo().getMarca().getNombre().toUpperCase();

        // Campus, edificio, aula
        BeanEdificio edificio = aula.getEdificio();
        BeanCampus campus = edificio.getCampus();

        String campusNombre = campus.getNombre().toUpperCase();
        String edificioNombre = edificio.getNombre().toUpperCase();
        String aulaNombre = aula.getNombre().toUpperCase();

        String prefijo = nombreProducto + marca + campusNombre + edificioNombre + aulaNombre;

        // Consecutivo
        int count = activoRepository.countByEtiquetaBienStartingWith(prefijo);
        String consecutivo = String.format("%03d", count + 1);

        return prefijo + "-" + consecutivo;
    }

    @Transactional(readOnly = true)
    public List<BeanActivo> getAll() {
        return activoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public BeanActivo getById(Long id) {
        return activoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activo no encontrado"));
    }

    @Transactional
    public BeanActivo create(CreateActivoDTO dto) {

        if (activoRepository.existsByNumeroSerie(dto.getNumeroSerie())) {
            throw new RuntimeException("El número de serie ya está registrado");
        }

        BeanProducto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        BeanAula aula = aulaRepository.findById(dto.getAulaId())
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        BeanActivo activo = new BeanActivo();
        activo.setNumeroSerie(dto.getNumeroSerie());
        activo.setProducto(producto);
        activo.setFechaAlta(dto.getFechaAlta());
        activo.setAula(aula);
        activo.setDescripcion(dto.getDescripcion());
        activo.setCosto(dto.getCosto());
        activo.setEstatus(ENUM_ESTATUS_ACTIVO.DISPONIBLE);
        activo.setEtiquetaBien(generarEtiquetaPublica(producto, aula));

        return activoRepository.save(activo);
    }

    @Transactional
    public BeanActivo update(Long id, UpdateActivoDTO dto) {
        BeanActivo activo = activoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activo no encontrado"));

        if (dto.getDescripcion() != null) {
            activo.setDescripcion(dto.getDescripcion());
        }

        if (dto.getCosto() != null) {
            activo.setCosto(dto.getCosto());
        }

        if (dto.getEstatus() != null) {
            activo.setEstatus(dto.getEstatus());
        }

        if (dto.getAulaId() != null) {
            BeanAula aula = aulaRepository.findById(dto.getAulaId())
                    .orElseThrow(() -> new RuntimeException("Aula no encontrada"));
            activo.setAula(aula);
        }

        return activoRepository.save(activo);
    }
}
