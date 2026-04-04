package com.example.integradora5d.dto.producto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductoDTO {

    // Nombre del tipo de producto (Laptop, Proyector...)
    // Si ya existe se reutiliza, si no se crea
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    // Marca: puede ser existente (por id) o nueva (por nombre)
    private Long marcaId;       // si elige una existente
    private String marcaNombre; // si escribe una nueva

    // Modelo: siempre se escribe, es único por marca
    @NotBlank(message = "El modelo es obligatorio")
    private String modeloNombre;
}