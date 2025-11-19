package com.reservas.reservasapirest.repositories;

import com.reservas.reservasapirest.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepo extends JpaRepository<Reserva, Long> {
    Optional<Reserva> findByAulaIdAndHorarioIdAndFecha(Long aulaId, Long horarioId, LocalDate fecha);

    List<Reserva> findByUsuarioId(Long usuarioId);
}
