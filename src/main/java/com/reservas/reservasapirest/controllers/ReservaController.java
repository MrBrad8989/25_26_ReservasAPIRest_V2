package com.reservas.reservasapirest.controllers;

import com.reservas.reservasapirest.dto.ReservaRequestDTO;
import com.reservas.reservasapirest.entities.Aula;
import com.reservas.reservasapirest.entities.Horario;
import com.reservas.reservasapirest.entities.Reserva;
import com.reservas.reservasapirest.services.ReservaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*; // Importa @RestController, @RequestMapping, etc.
import org.springframework.web.bind.annotation.RequestBody; // IMPORTANTE: Explícito para evitar errores

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@AllArgsConstructor
public class ReservaController {

    private final ReservaService service;

    // Obtener todas las reservas
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Reserva>> getAllReservas() {
        return ResponseEntity.ok(service.getAllReservas());
    }

    // Obtener mis reservas
    @GetMapping("/mis-reservas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Reserva>> obtenerMisReservas() {
        return ResponseEntity.ok(service.obtenerMisReservas());
    }

    // Obtener reserva por ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Long id) {
        return service.getReservaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva reserva
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reserva> createReserva(@Valid @RequestBody ReservaRequestDTO reservaRequestDTO) {
        System.out.println("Creando reserva: " + reservaRequestDTO); // Log para depurar
        Reserva nuevaReserva = service.createReserva(reservaRequestDTO);
        return ResponseEntity.status(201).body(nuevaReserva);
    }

    // --- AQUÍ ESTABA EL PROBLEMA ---
    // Editar reserva existente
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reserva> updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequestDTO reservaDTO) {

        System.out.println("Recibida petición PUT para ID: " + id);
        System.out.println("Datos recibidos (Body): " + reservaDTO);

        // Convertir DTO a entidad temporal para pasar al servicio
        Reserva reservaUpdates = new Reserva();
        reservaUpdates.setFecha(reservaDTO.fecha());
        reservaUpdates.setMotivo(reservaDTO.motivo());
        reservaUpdates.setNumeroAsistentes(reservaDTO.numeroAsistentes());
        reservaUpdates.setId(id);

        if(reservaDTO.aulaId() != null) {
            Aula a = new Aula();
            a.setId(reservaDTO.aulaId());
            reservaUpdates.setAula(a);
        }
        if(reservaDTO.horarioId() != null) {
            Horario h = new Horario();
            h.setId(reservaDTO.horarioId());
            reservaUpdates.setHorario(h);
        }

        Reserva actualizada = service.actualizarReserva(id, reservaUpdates);
        return ResponseEntity.ok(actualizada);
    }

    // Borrar reserva
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        service.deleteReserva(id);
        return ResponseEntity.noContent().build();
    }
}