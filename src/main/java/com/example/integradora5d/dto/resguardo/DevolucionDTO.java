package com.example.integradora5d.dto.resguardo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevolucionDTO {

    @NotNull
    private Long resguardoId;

    private String observaciones;
}