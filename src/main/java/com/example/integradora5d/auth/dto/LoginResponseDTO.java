package com.example.integradora5d.auth.dto;

import com.example.integradora5d.dto.usuario.UsuarioForClientDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String rol;
    private UsuarioForClientDTO usuario;
}
