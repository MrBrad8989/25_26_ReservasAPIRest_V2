package com.reservas.reservasapirest.controllers;

import com.reservas.reservasapirest.entities.Aula;
import com.reservas.reservasapirest.entities.Reserva;
import com.reservas.reservasapirest.services.AulaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aulas")
@AllArgsConstructor
public class AulaController {

    private final AulaService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Aula> getAulas() {
        return service.getAulas();
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Aula> getAulaById(@PathVariable Long id) {
        return service.getAulaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Aula> createAula(@RequestBody Aula aula) {
        Aula createdAula = service.createAula(aula);
        return ResponseEntity.status(201).body(createdAula);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Aula> actualizarAula(@PathVariable Long id, @RequestBody Aula aulaModificada) {
        Aula updatedAula = service.actualizarAula(id, aulaModificada);
        return ResponseEntity.ok(updatedAula);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAula(@PathVariable Long id) {
        service.deleteAula(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/reservas" )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReservasByAulaId(@PathVariable Long id) {
        var aulaOpt = service.getAulaById(id);
        if (aulaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aulaOpt.get().getReservas());
    }
    // Ver aulas por capacidad: /aulas?capacidad=20
    @GetMapping(params = "capacidad")
    @PreAuthorize("isAuthenticated()")
    public List<Aula> getAulasByCapacidad(@RequestParam Integer capacidad) {
        return service.getAulas().stream()
                .filter(aula -> aula.getCapacidad() != null && aula.getCapacidad().equals(capacidad))
                .toList();
    }
    // Ver aulas con ordenadores: /aulas?ordenadores=true
    @GetMapping(params = "ordenadores")
    @PreAuthorize("isAuthenticated()")
    public List<Aula> getAulasConOrdenadores(@RequestParam Boolean ordenadores) {
        return service.getAulas().stream()
                .filter(aula -> aula.getEsAulaOrdenadores() != null && aula.getEsAulaOrdenadores().equals(ordenadores))
                .toList();
    }
}
