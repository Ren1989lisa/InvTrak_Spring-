package com.example.integradora5d.controllers.usuario;


import com.example.integradora5d.dto.usuario.CreateUsuarioDto;
import com.example.integradora5d.dto.usuario.ForgotPasswordDTO;
import com.example.integradora5d.dto.usuario.UpdateUsuarioDTO;
import com.example.integradora5d.dto.usuario.UsuarioForClientDTO;
import com.example.integradora5d.error.errorTypes.CustomNotContentException;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioForClientDTO> getPerfil(Principal principal) {
        return ResponseEntity.ok(usuarioService.getPerfil(principal.getName()));
    }

    // Alias para frontend: /me (evita chocar con /{id})
    @GetMapping("/me")
    public ResponseEntity<UsuarioForClientDTO> getMe(Principal principal) {
        return ResponseEntity.ok(usuarioService.getPerfil(principal.getName()));
    }

    // Admin edita cualquier usuario
    @PutMapping("/{id}")
    public ResponseEntity<BeanUsuario> update(@PathVariable Long id,
                                              @Valid @RequestBody UpdateUsuarioDTO dto) {
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
