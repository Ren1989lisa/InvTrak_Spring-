package com.example.integradora5d.service.activo;

import com.example.integradora5d.dto.activo.CreateActivoDTO;
import com.example.integradora5d.dto.activo.UpdateActivoDTO;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import com.example.integradora5d.models.aula_laboratorio.AulaRepository;
import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.campus.BeanCampus;
import com.example.integradora5d.models.edificio.BeanEdificio;
import com.example.integradora5d.models.producto.BeanProducto;
import com.example.integradora5d.models.producto.ProductoRepository;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActivoService {

    private final ActivoRepository activoRepository;
    private final ProductoRepository productoRepository;
    private final AulaRepository aulaRepository;
    private final UsuarioRepository usuarioRepository;

    public ActivoService(ActivoRepository activoRepository,
                         ProductoRepository productoRepository,
                         AulaRepository aulaRepository,
                         UsuarioRepository usuarioRepository) {
        this.activoRepository = activoRepository;
        this.productoRepository = productoRepository;
        this.aulaRepository = aulaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public String generarEtiquetaPublica(BeanProducto producto, BeanAula aula) {
        String nombreProducto = producto.getNombre().length() >= 2
                ? producto.getNombre().substring(0, 2).toUpperCase()
                : producto.getNombre().toUpperCase();

        String marca = producto.getModelo().getMarca().getNombre().length() >= 2
                ? producto.getModelo().getMarca().getNombre().substring(0, 2).toUpperCase()
                : producto.getModelo().getMarca().getNombre().toUpperCase();

        BeanEdificio edificio = aula.getEdificio();
        BeanCampus campus = edificio.getCampus();

        String campusNombre = campus.getNombre().toUpperCase();
        String edificioNombre = edificio.getNombre().toUpperCase();
        String aulaNombre = aula.getNombre().toUpperCase();

        String prefijo = nombreProducto + marca + campusNombre + edificioNombre + aulaNombre;

        int count = activoRepository.countByEtiquetaBienStartingWith(prefijo);
        String consecutivo = String.format("%03d", count + 1);

        return prefijo + "-" + consecutivo;
    }

    @Transactional(readOnly = true)
    public List<BeanActivo> getAll() {
        List<BeanActivo> activos = activoRepository.findAll();
        activos.forEach(this::initializeLazyRelations);
        return activos;
    }

    @Transactional(readOnly = true)
    public List<BeanActivo> getMisActivos(String correoAutenticado) {
        BeanUsuario usuario = usuarioRepository.findByCorreoIgnoreCase(correoAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        String rolNombre = usuario.getRol() != null ? usuario.getRol().getNombre() : "";

        List<BeanActivo> activos;
        if ("ADMINISTRADOR".equalsIgnoreCase(rolNombre)) {
            activos = activoRepository.findAll();
        } else if ("TECNICO".equalsIgnoreCase(rolNombre)) {
            activos = activoRepository.findActivosEnMantenimientoByTecnico(usuario.getIdUsuario());
        } else {
            activos = activoRepository.findActivosResguardadosByUsuario(usuario.getIdUsuario());
        }

        activos.forEach(this::initializeLazyRelations);
        return activos;
    }

    private void initializeLazyRelations(BeanActivo a) {
        if (a.getAula() != null) {
            a.getAula().getNombre();
            if (a.getAula().getEdificio() != null) {
                a.getAula().getEdificio().getNombre();
                if (a.getAula().getEdificio().getCampus() != null) {
                    a.getAula().getEdificio().getCampus().getNombre();
                }
            }
        }
        if (a.getProducto() != null) {
            a.getProducto().getNombre();
            if (a.getProducto().getModelo() != null) {
                a.getProducto().getModelo().getNombre();
                if (a.getProducto().getModelo().getMarca() != null) {
                    a.getProducto().getModelo().getMarca().getNombre();
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public BeanActivo getById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activo no encontrado");
        }
        BeanActivo a = activoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activo no encontrado"));
        if (a.getAula() != null) {
            a.getAula().getNombre();
            if (a.getAula().getEdificio() != null) {
                a.getAula().getEdificio().getNombre();
                if (a.getAula().getEdificio().getCampus() != null) {
                    a.getAula().getEdificio().getCampus().getNombre();
                }
            }
        }
        if (a.getProducto() != null) {
            a.getProducto().getNombre();
            if (a.getProducto().getModelo() != null) {
                a.getProducto().getModelo().getNombre();
                if (a.getProducto().getModelo().getMarca() != null) {
                    a.getProducto().getModelo().getMarca().getNombre();
                }
            }
        }
        return a;
    }

    private String sanitizeNumeroSerie(String numeroSerie) {
        String value = numeroSerie == null ? "" : numeroSerie.trim();
        if (value.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El numero de serie es obligatorio");
        }
        return value;
    }

    private void validateFechaAlta(LocalDate fechaAlta) {
        if (fechaAlta == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de alta es obligatoria");
        }
    }

    private double validateCosto(Double costo) {
        if (costo == null || costo <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El costo debe ser mayor a 0");
        }
        return costo;
    }

    @Transactional
    public BeanActivo create(CreateActivoDTO dto) {
        String numeroSerie = sanitizeNumeroSerie(dto.getNumeroSerie());
        validateFechaAlta(dto.getFechaAlta());
        double costo = validateCosto(dto.getCosto());

        if (activoRepository.existsByNumeroSerie(numeroSerie)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El numero de serie ya esta registrado");
        }

        BeanProducto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado"));

        BeanAula aula = aulaRepository.findById(dto.getAulaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aula no encontrada"));

        BeanActivo activo = new BeanActivo();
        activo.setNumeroSerie(numeroSerie);
        activo.setProducto(producto);
        activo.setFechaAlta(dto.getFechaAlta());
        activo.setAula(aula);
        activo.setDescripcion(dto.getDescripcion().trim());
        activo.setCosto(costo);
        activo.setEstatus(ENUM_ESTATUS_ACTIVO.DISPONIBLE);
        activo.setEtiquetaBien(generarEtiquetaPublica(producto, aula));

        return activoRepository.save(activo);
    }

    @Transactional
    public BeanActivo update(Long id, UpdateActivoDTO dto) {
        BeanActivo activo = activoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activo no encontrado"));

        String numeroSerie = sanitizeNumeroSerie(dto.getNumeroSerie());
        validateFechaAlta(dto.getFechaAlta());
        double costo = validateCosto(dto.getCosto());

        if (activoRepository.existsByNumeroSerieAndIdActivoNot(numeroSerie, activo.getIdActivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El numero de serie ya esta registrado");
        }

        BeanProducto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado"));

        BeanAula aula = aulaRepository.findById(dto.getAulaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aula no encontrada"));

        if (dto.getEstatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estatus es obligatorio");
        }

        String descripcion = dto.getDescripcion() == null ? "" : dto.getDescripcion().trim();
        if (descripcion.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripcion es obligatoria");
        }

        activo.setNumeroSerie(numeroSerie);
        activo.setProducto(producto);
        activo.setFechaAlta(dto.getFechaAlta());
        activo.setAula(aula);
        activo.setDescripcion(descripcion);
        activo.setCosto(costo);
        activo.setEstatus(dto.getEstatus());
        // etiquetaBien es fija y no se actualiza en edicion.

        return activoRepository.save(activo);
    }

    @Transactional(readOnly = true)
    public List<BeanActivo> getDisponibles() {
        return activoRepository.findByEstatus(ENUM_ESTATUS_ACTIVO.DISPONIBLE);
    }
}
