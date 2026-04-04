package com.example.integradora5d.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MantenimientoTecnicoDTO {
    private String nombreTecnico;
    private Long totalMantenimientos;
}