package com.reservas.reservasapirest.controllers;

import com.reservas.reservasapirest.dto.CambiarPasswordRequestDTO;
import com.reservas.reservasapirest.dto.UsuarioResponseDTO;
import com.reservas.reservasapirest.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
@AllArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil() {
        UsuarioResponseDTO perfil = usuarioService.obtenerPerfilActual();
        return ResponseEntity.ok(perfil);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo Admin puede borrar
    public ResponseEntity<Void> borrarUsuario(@PathVariable Long id) {
        // Lógica para borrar usuario
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo Admin puede modificar
    public ResponseEntity<?> modificarUsuario(@PathVariable Long id, @RequestBody Object userData) { // Usa un DTO
        // Lógica para modificar usuario
        return ResponseEntity.ok().build();
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodosLosUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @PatchMapping("/cambiar-pass")
    @PreAuthorize("isAuthenticated()") // Cualquier usuario autenticado
    public ResponseEntity<?> cambiarPassword(@Valid @RequestBody CambiarPasswordRequestDTO requestDTO) {
        usuarioService.cambiarPassword(requestDTO);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña cambiada con éxito"));
    }
}
