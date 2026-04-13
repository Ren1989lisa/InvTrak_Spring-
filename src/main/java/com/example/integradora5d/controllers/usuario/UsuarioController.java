package com.example.integradora5d.controllers.usuario;


import com.example.integradora5d.dto.usuario.CreateUsuarioDto;
import com.example.integradora5d.dto.usuario.UpdateUsuarioDTO;
import com.example.integradora5d.dto.usuario.UsuarioForClientDTO;
import com.example.integradora5d.error.errorTypes.CustomNotContentException;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/tecnicos")
    public ResponseEntity<List<BeanUsuario>> getTecnicos() {
        return ResponseEntity.ok(usuarioService.getTecnicos());
    }

    // Ver perfil propio - cualquier usuario autenticado
    // UsuarioController.java corregido
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioForClientDTO> getPerfil() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        String email = (String) auth.getPrincipal();

        return ResponseEntity.ok(usuarioService.getPerfil(email));
    }

    // Admin edita cualquier usuario
    @PutMapping("/{id}")
    public ResponseEntity<BeanUsuario> update(@PathVariable Long id,
                                              @RequestBody UpdateUsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.update(id, dto));
    }

    // Admin elimina usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<BeanUsuario> create(@Valid @RequestBody CreateUsuarioDto dto) {
        return new ResponseEntity<>(usuarioService.createUsuario(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioForClientDTO>> getAll() throws CustomNotContentException {
        return ResponseEntity.ok(usuarioService.getUsuarios());
    }
}