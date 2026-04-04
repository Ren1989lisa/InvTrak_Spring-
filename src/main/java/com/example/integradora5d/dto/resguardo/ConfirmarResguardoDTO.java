package com.example.integradora5d.dto.resguardo;

import com.example.integradora5d.models.checklist_resguardo.ENUM_CHECKLIST;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmarResguardoDTO {

    @NotNull
    private Long resguardoId;

    @NotNull(message = "El campo enciende es obligatorio")
    private ENUM_CHECKLIST enciende;

    @NotNull(message = "El campo pantalla es obligatorio")
    private ENUM_CHECKLIST pantallaFunciona;

    @NotNull(message = "El campo cargador es obligatorio")
    private ENUM_CHECKLIST tieneCargador;

    @NotNull(message = "El campo daños es obligatorio")
    private ENUM_CHECKLIST danios;

    private String observaciones;
}