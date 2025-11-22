package com.reservas.reservasapirest.services;

import com.reservas.reservasapirest.entities.Horario;
import com.reservas.reservasapirest.repositories.HorarioRepo;
import com.reservas.reservasapirest.utils.ClassUtil; // Importante
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
    private final ClassUtil classUtil; // Inyectamos la utilidad de copia

    public List<Horario> getAllHorarios() {
        return horarioRepository.findAll();
    }

    public Optional<Horario> getHorarioById(Long id) {
        return horarioRepository.findById(id);
    }

    public Horario createHorario(Horario horario) {
        return horarioRepository.save(horario);
    }

    // --- MÉTODO NUEVO PARA EDITAR ---
    public Horario actualizarHorario(Long id, Horario horarioModificado) {
        Optional<Horario> existenteOpt = horarioRepository.findById(id);

        if (existenteOpt.isPresent()) {
            Horario existente = existenteOpt.get();

            // Protegemos la lista de reservas asociadas para no perderlas
            var reservasOriginales = existente.getReservas();

            try {
                classUtil.copyProperties(existente, horarioModificado);
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar el horario", e);
            }

            // Restauramos las relaciones
            existente.setReservas(reservasOriginales);

            return horarioRepository.save(existente);
        }
        return horarioModificado;
    }

    public void deleteHorario(Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado"));

        // Opcional: Validar si tiene reservas antes de borrar para evitar errores de integridad
        // if (!horario.getReservas().isEmpty()) throw ...

        horarioRepository.delete(horario);
    }
}