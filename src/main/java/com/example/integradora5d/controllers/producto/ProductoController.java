package com.example.integradora5d.controllers.producto;

import com.example.integradora5d.dto.producto.CreateProductoDTO;
import com.example.integradora5d.dto.producto.UpdateProductoDTO;
import com.example.integradora5d.models.marca.BeanMarca;
import com.example.integradora5d.models.marca.MarcaRepository;
import com.example.integradora5d.models.modelo.BeanModelo;
import com.example.integradora5d.models.modelo.ModeloRepository;
import com.example.integradora5d.models.producto.BeanProducto;
import com.example.integradora5d.service.producto.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/producto")
public class ProductoController {

    private final ProductoService productoService;
    private final ModeloRepository modeloRepository;
    private final MarcaRepository marcaRepository;

    public ProductoController(ProductoService productoService, ModeloRepository modeloRepository, MarcaRepository marcaRepository) {
        this.productoService = productoService;
        this.modeloRepository = modeloRepository;
        this.marcaRepository = marcaRepository;
    }

    @GetMapping
    public ResponseEntity<List<BeanProducto>> getAll() {
        return ResponseEntity.ok(productoService.getAll());
    }

    @PostMapping
    public ResponseEntity<BeanProducto> create(@Valid @RequestBody CreateProductoDTO dto) {
        return new ResponseEntity<>(productoService.create(dto), HttpStatus.CREATED);
    }

    // Para cargar el dropdown de marcas
    @GetMapping("/marcas")
    public ResponseEntity<List<BeanMarca>> getMarcas() {
        return ResponseEntity.ok(marcaRepository.findAll());
    }

    // Para cargar modelos según la marca seleccionada
    @GetMapping("/modelos/{marcaId}")
    public ResponseEntity<List<BeanModelo>> getModelosByMarca(@PathVariable Long marcaId) {
        return ResponseEntity.ok(modeloRepository.findByMarca_Id_marca(marcaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeanProducto> update(@PathVariable Long id,
                                               @RequestBody UpdateProductoDTO dto) {
        return ResponseEntity.ok(productoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
