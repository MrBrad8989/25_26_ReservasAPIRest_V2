package com.reservas.reservasapirest.services;

import com.reservas.reservasapirest.dto.ReservaRequestDTO;
import com.reservas.reservasapirest.entities.Aula;
import com.reservas.reservasapirest.entities.Horario;
import com.reservas.reservasapirest.entities.Reserva;
import com.reservas.reservasapirest.entities.Usuario;
import com.reservas.reservasapirest.repositories.AulaRepo;
import com.reservas.reservasapirest.repositories.HorarioRepo;
import com.reservas.reservasapirest.repositories.ReservaRepo;
import com.reservas.reservasapirest.repositories.UsuarioRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.reservas.reservasapirest.utils.ClassUtil;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservaService {
    private ReservaRepo repo;
    private AulaRepo aulaRepo;
    private final HorarioRepo horarioRepo;
    private final UsuarioRepo usuarioRepo;
    private final ClassUtil classUtil;


    public List<Reserva> getReservas() {
        return repo.findAll();
    }
    public Optional<Reserva> getReservaById(Long id) {
        return repo.findById(id);
    }

    public List<Reserva> obtenerMisReservas() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return repo.findByUsuarioId(usuario.getId());
    }

    public Reserva actualizarReserva(Long id, Reserva ReservaModificada) {
        Optional<Reserva> ReservaExistenteOpt = repo.findById(id);

        if (ReservaExistenteOpt.isPresent()) {
            Reserva ReservaExistente = ReservaExistenteOpt.get();

            try {
                classUtil.copyProperties(ReservaExistente, ReservaModificada);
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar la Reserva", e);
            }

            return repo.save(ReservaExistente);
        }

        return ReservaModificada;
    }

    public Reserva createReserva(ReservaRequestDTO reservaRequestDTO) {
        Aula aula = aulaRepo.findById(reservaRequestDTO.aulaId())
                .orElseThrow(() -> new RuntimeException("Aula no encontrada"));

        Horario horario = horarioRepo.findById(reservaRequestDTO.horarioId())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (reservaRequestDTO.numeroAsistentes() > aula.getCapacidad()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de asistentes excede la capacidad del aula");
        }

        Optional<Reserva> solapamiento = repo.findByAulaIdAndHorarioIdAndFecha(
                reservaRequestDTO.aulaId(),
                reservaRequestDTO.horarioId(),
                reservaRequestDTO.fecha()
        );
        if (solapamiento.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una reserva para este aula, horario y fecha");
        }

        // Obtener el usuario autenticado
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear y guardar la nueva reserva
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setFecha(reservaRequestDTO.fecha());
        nuevaReserva.setMotivo(reservaRequestDTO.motivo());
        nuevaReserva.setNumeroAsistentes(reservaRequestDTO.numeroAsistentes());
        nuevaReserva.setAula(aula);
        nuevaReserva.setHorario(horario);
        nuevaReserva.setUsuario(usuario);

        return repo.save(nuevaReserva);

    }


    public void deleteReserva(Long id) {
        // 1. Buscar la reserva
        Reserva reserva = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        // 2. Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Usuario usuarioActual = usuarioRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        // 3. Comprobar permisos
        // "el usuario podrá borrarlos, pero sólo si los ha creado él"
        // Un ADMIN puede borrar cualquiera
        if (usuarioActual.getRole() == Usuario.Role.ROLE_ADMIN ||
                reserva.getUsuario().getId().equals(usuarioActual.getId())) {
            repo.delete(reserva);

        } else {
            // Si no es ADMIN ni el propietario, se deniega
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, // 403 Forbidden
                    "No tiene permisos para borrar esta reserva.");
        }
    }

    public List<Reserva> getAllReservas() {
        return repo.findAll();
    }
}
