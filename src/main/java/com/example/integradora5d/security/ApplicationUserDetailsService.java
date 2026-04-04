package com.example.integradora5d.security;

import com.example.integradora5d.error.errorTypes.CustomNotFoundException;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public ApplicationUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        BeanUsuario usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new CustomNotFoundException("Usuario no encontrado: " + email));

        if (usuario.getRol() == null) {
            throw new CustomNotFoundException("Usuario sin rol asignado: " + email);
        }

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPassword())
                .roles(usuario.getRol().getNombre())
                .disabled(!usuario.getEstatus())
                .build();
    }
}