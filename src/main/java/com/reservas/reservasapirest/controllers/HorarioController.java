package com.reservas.reservasapirest.controllers;

import com.reservas.reservasapirest.entities.Horario;
import com.reservas.reservasapirest.services.HorarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@AllArgsConstructor
public class HorarioController {
    private final HorarioService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Horario>> getAllHorarios() {
        return ResponseEntity.ok(service.getAllHorarios());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Horario> getHorarioById(@PathVariable Long id) {
        return service.getHorarioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Horario> createHorario(@RequestBody Horario horario) {
        Horario nuevoHorario = service.createHorario(horario);
        return ResponseEntity.status(201).body(nuevoHorario);
    }

    // --- MÉTODO NUEVO ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Horario> actualizarHorario(@PathVariable Long id, @RequestBody Horario horario) {
        // IMPORTANTE: Fijar el ID para evitar el error "Identifier altered from 1 to null"
        horario.setId(id);
        Horario actualizado = service.actualizarHorario(id, horario);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHorario(@PathVariable Long id) {
        service.deleteHorario(id);
        return ResponseEntity.noContent().build();
    }
}