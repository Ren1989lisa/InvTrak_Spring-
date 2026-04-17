package com.example.integradora5d.dto.resguardo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmarResguardoQrDTO {

    @NotNull(message = "El idActivo es obligatorio")
    private Long idActivo;
}
