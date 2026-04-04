package com.example.integradora5d.mappers;

import com.example.integradora5d.dto.usuario.CreateUsuarioDto;
import com.example.integradora5d.dto.usuario.UsuarioForClientDTO;
import com.example.integradora5d.models.usuario.BeanUsuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public BeanUsuario toEntity(CreateUsuarioDto payload) {

        BeanUsuario newUsuario = new BeanUsuario();

        newUsuario.setNombre(payload.getNombre());
        newUsuario.setCorreo(payload.getCorreo());
        newUsuario.setFechaNacimiento(payload.getFechaNacimiento());
        newUsuario.setCurp(payload.getCurp());
        newUsuario.setArea(payload.getArea());

        newUsuario.setEstatus(true);

        return newUsuario;
    }

    public List<UsuarioForClientDTO> usuarioToUsuarioDto(List<BeanUsuario> usuarios) {
        return usuarios.stream().map(usuario -> {
            UsuarioForClientDTO dto = new UsuarioForClientDTO();

            dto.setIdUsuario(usuario.getIdUsuario());
            dto.setNombre(usuario.getNombre());
            dto.setCorreo(usuario.getCorreo());
            dto.setCurp(usuario.getCurp());
            dto.setNumeroEmpleado(usuario.getNumeroEmpleado());
            dto.setArea(usuario.getArea());
            dto.setEstatus(usuario.getEstatus());

            if (usuario.getRol() != null) {
                dto.setRol(usuario.getRol().getNombre());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}