package com.example.integradora5d.service.producto;

import com.example.integradora5d.dto.producto.CreateProductoDTO;
import com.example.integradora5d.dto.producto.UpdateProductoDTO;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.marca.BeanMarca;
import com.example.integradora5d.models.marca.MarcaRepository;
import com.example.integradora5d.models.modelo.BeanModelo;
import com.example.integradora5d.models.modelo.ModeloRepository;
import com.example.integradora5d.models.producto.BeanProducto;
import com.example.integradora5d.models.producto.ENUM_ESTATUS_PRODUCTO;
import com.example.integradora5d.models.producto.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;
    private final ModeloRepository modeloRepository;
    private final ActivoRepository  activoRepository;

    public ProductoService(ProductoRepository productoRepository,
                           MarcaRepository marcaRepository,
                           ModeloRepository modeloRepository, ActivoRepository activoRepository) {
        this.productoRepository = productoRepository;
        this.marcaRepository = marcaRepository;
        this.modeloRepository = modeloRepository;
        this.activoRepository = activoRepository;
    }

    @Transactional(readOnly = true)
    public List<BeanProducto> getAll() {
        return productoRepository.findAll();
    }

    @Transactional
    public BeanProducto create(CreateProductoDTO dto) {

        // 1. Resolver marca
        BeanMarca marca;
        if (dto.getMarcaId() != null) {
            marca = marcaRepository.findById(dto.getMarcaId())
                    .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
        } else if (dto.getMarcaNombre() != null && !dto.getMarcaNombre().isBlank()) {
            marca = marcaRepository.findByNombre(dto.getMarcaNombre())
                    .orElseGet(() -> {
                        BeanMarca nueva = new BeanMarca();
                        nueva.setNombre(dto.getMarcaNombre());
                        return marcaRepository.save(nueva);
                    });
        } else {
            throw new RuntimeException("Debes proporcionar una marca");
        }

        // 2. Resolver modelo (único por marca)
        BeanModelo modelo = modeloRepository
                .findByNombreAndMarcaId(dto.getModeloNombre(), marca.getId_marca())
                .orElseGet(() -> {
                    BeanModelo nuevo = new BeanModelo();
                    nuevo.setNombre(dto.getModeloNombre());
                    nuevo.setMarca(marca);
                    return modeloRepository.save(nuevo);
                });

        // 3. Crear producto
        BeanProducto producto = new BeanProducto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setModelo(modelo);
        producto.setEstatus(ENUM_ESTATUS_PRODUCTO.ACTIVO);

        return productoRepository.save(producto);
    }

    @Transactional
    public BeanProducto update(Long id, UpdateProductoDTO dto) {
        BeanProducto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (dto.getDescripcion() != null && !dto.getDescripcion().isBlank()) {
            producto.setDescripcion(dto.getDescripcion());
        }

        if (dto.getEstatus() != null) {
            producto.setEstatus(dto.getEstatus());
        }

        return productoRepository.save(producto);
    }

    @Transactional
    public void delete(Long id) {
        BeanProducto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (activoRepository.existsByProducto(producto)) {
            throw new RuntimeException("No se puede eliminar, el producto tiene activos vinculados");
        }

        productoRepository.delete(producto);
    }
}