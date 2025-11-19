package com.reservas.reservasapirest.controllers;

import com.reservas.reservasapirest.dto.ReservaRequestDTO;
import com.reservas.reservasapirest.entities.Reserva;
import com.reservas.reservasapirest.services.ReservaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@AllArgsConstructor
public class ReservaController {
    private final ReservaService service;

    // Tomar todas las reservas incluyendo el aula reservada en cada reserva
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Reserva>> getAllReservas() {
        List<Reserva> reservas = service.getAllReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/mis-reservas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Reserva>> obtenerMisReservas() {
        List<Reserva> misReservas = service.obtenerMisReservas();
        return ResponseEntity.ok(misReservas);
    }

    // Coger una reserva incluyendo el aula reservada
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Long id) {
        return service.getReservaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear una nueva reserva
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reserva> createReserva(@Valid @RequestBody ReservaRequestDTO reservaRequestDTO) {
        Reserva nuevaReserva = service.createReserva(reservaRequestDTO);
        return ResponseEntity.status(201).body(nuevaReserva);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        service.deleteReserva(id);
        return ResponseEntity.noContent().build();
    }
}
