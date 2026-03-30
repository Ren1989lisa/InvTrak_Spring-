package com.example.integradora5d.service.usuario;

import com.example.integradora5d.dto.usuario.UsuarioForClientDTO;
import com.example.integradora5d.error.errorTypes.CustomNotContentException;
import com.example.integradora5d.mappers.UsuarioMapper;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;
    private UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional(readOnly = true)
    public List<UsuarioForClientDTO> getUsuarios(){
        List<BeanUsuario> usuarios = usuarioRepository.findAll();

        if(usuarios.isEmpty()){
            throw new CustomNotContentException("No hay usuarios");
        }

        return usuarioMapper.usuarioToUsuarioDto(usuarios);
    }


}
