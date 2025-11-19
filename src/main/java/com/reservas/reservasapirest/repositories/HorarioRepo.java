package com.reservas.reservasapirest.repositories;

import com.reservas.reservasapirest.entities.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepo extends JpaRepository<Horario, Long> {
}
