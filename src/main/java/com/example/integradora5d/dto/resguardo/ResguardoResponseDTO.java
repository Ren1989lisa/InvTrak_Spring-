package com.example.integradora5d.dto.resguardo;

import java.time.LocalDate;

public record ResguardoResponseDTO(
        Long idResguardo,
        LocalDate fechaAsignacion,
        Boolean confirmado,
        String observaciones,
        LocalDate fechaDevolucion,
        UsuarioDTO usuario,
        ChecklistDTO checklists,
        ActivoDTO activo
) {

    public record UsuarioDTO(
            Long idUsuario,
            String nombre,
            String correo,
            LocalDate fechaNacimiento,
            String curp,
            String numeroEmpleado,
            String area,
            Boolean estatus,
            Boolean primerAcceso,
            String tokenDispositivo,
            RolDTO rol
    ) {}

    public record RolDTO(
            Long idRol,
            String nombre
    ) {}

    public record ChecklistDTO(
            Long idChecklist,
            String observaciones,
            String enciende,
            String pantallaFunciona,
            String tieneCargador,
            String danios
    ) {}

    public record ActivoDTO(
            Long idActivo,
            String etiquetaBien,
            String numeroSerie,
            String descripcion,
            LocalDate fechaAlta,
            Double costo,
            String estatus,
            AulaDTO aula,
            ProductoDTO producto
    ) {}

    public record AulaDTO(
            Long idAula,
            String nombre,
            String descripcion,
            EdificioDTO edificio
    ) {}

    public record EdificioDTO(
            Long idEdificio,
            String nombre,
            CampusDTO campus
    ) {}

    public record CampusDTO(
            Long idCampus,
            String nombre
    ) {}

    public record ProductoDTO(
            Long id_producto,
            String nombre,
            String descripcion,
            String estatus,
            ModeloDTO modelo
    ) {}

    public record ModeloDTO(
            Long id_modelo,
            String nombre,
            MarcaDTO marca
    ) {}

    public record MarcaDTO(
            Long id_marca,
            String nombre
    ) {}
}
