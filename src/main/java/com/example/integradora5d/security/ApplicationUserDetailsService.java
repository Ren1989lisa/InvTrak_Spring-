package com.example.integradora5d.security;

import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public ApplicationUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();

        BeanUsuario usuario = usuarioRepository.findByCorreoIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (usuario.getRol() == null) {
            throw new UsernameNotFoundException("Usuario sin rol asignado");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new UsernameNotFoundException("Usuario sin credenciales válidas");
        }

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPassword())
                .roles(usuario.getRol().getNombre())
                .disabled(!usuario.getEstatus())
                .build();
    }
}
