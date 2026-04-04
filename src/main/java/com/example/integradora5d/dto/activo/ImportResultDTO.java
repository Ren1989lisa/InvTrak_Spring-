package com.example.integradora5d.dto.activo;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ImportResultDTO {
    private int insertados;
    private int rechazados;
    private List<String> errores;

    public ImportResultDTO(int insertados, int rechazados, List<String> errores) {
        this.insertados = insertados;
        this.rechazados = rechazados;
        this.errores = errores;
    }
}