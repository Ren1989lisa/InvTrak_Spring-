package com.example.integradora5d.dto.activo;

import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.resguardo.BeanResguardo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ActivoResponseDTO {

    private Long idActivo;
    private String etiquetaBien;
    private String numeroSerie;
    private String descripcion;
    private String estatus;
    private Double costo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaAlta;

    private String propietario;

    private Object aula;
    private Object producto;

    public static ActivoResponseDTO from(BeanActivo activo) {
        ActivoResponseDTO dto = new ActivoResponseDTO();
        dto.setIdActivo(activo.getIdActivo());
        dto.setEtiquetaBien(activo.getEtiquetaBien());
        dto.setNumeroSerie(activo.getNumeroSerie());
        dto.setDescripcion(activo.getDescripcion());
        dto.setEstatus(activo.getEstatus() != null ? activo.getEstatus().name() : null);
        dto.setCosto(activo.getCosto());
        dto.setFechaAlta(activo.getFechaAlta());
        dto.setAula(activo.getAula());
        dto.setProducto(activo.getProducto());

        // Buscar resguardo activo (confirmado y sin devolución)
        String propietario = "";
        if (activo.getResguardos() != null) {
            propietario = activo.getResguardos().stream()
                    .filter(r -> Boolean.TRUE.equals(r.getConfirmado()) && r.getFechaDevolucion() == null)
                    .map(BeanResguardo::getUsuario)
                    .filter(u -> u != null && u.getNombre() != null)
                    .map(u -> u.getNombre())
                    .findFirst()
                    .orElse("");
        }
        dto.setPropietario(propietario);

        return dto;
    }
}
