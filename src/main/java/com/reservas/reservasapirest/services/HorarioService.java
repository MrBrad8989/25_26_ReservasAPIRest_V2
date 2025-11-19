package com.reservas.reservasapirest.services;

import com.reservas.reservasapirest.entities.Horario;
import com.reservas.reservasapirest.repositories.HorarioRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HorarioService {
    private final HorarioRepo horarioRepository;

    public List<Horario> getAllHorarios() {
        return horarioRepository.findAll();
    }

    public Optional<Horario> getHorarioById(Long id) {
        return horarioRepository.findById(id);
    }

    public Horario createHorario(Horario horario) {
        return horarioRepository.save(horario);
    }

    public void deleteHorario(Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado"));
        horarioRepository.delete(horario);
    }
}
