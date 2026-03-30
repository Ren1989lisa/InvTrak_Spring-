package com.example.integradora5d.mappers;

import com.example.integradora5d.dto.usuario.UsuarioForClientDTO;
import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UsuarioMapper {

    public UsuarioForClientDTO usuarioToUsuarioDto(BeanUsuario usuario) {
        return new UsuarioForClientDTO(
               usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getNumeroEmpleado(),
                usuario.getArea(),
                usuario.getFechaNacimiento(),
                usuario.getEstatus(),
                usuario.getCurp(),
                usuario.getRol().getNombre()
        );
    }
}