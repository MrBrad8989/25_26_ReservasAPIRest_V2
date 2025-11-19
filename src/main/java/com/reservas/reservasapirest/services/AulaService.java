package com.reservas.reservasapirest.services;

import com.reservas.reservasapirest.entities.Aula;
import com.reservas.reservasapirest.repositories.AulaRepo;
import com.reservas.reservasapirest.utils.ClassUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AulaService {
    private final AulaRepo repo;
    private final ClassUtil classUtil;

    public List<Aula> getAulas() {
        return repo.findAll();
    }
    public Optional<Aula> getAulaById(Long id) {
        return repo.findById(id);
    }

    public Aula actualizarAula(Long id, Aula aulaModificada) {
        Optional<Aula> aulaExistenteOpt = repo.findById(id);

        if (aulaExistenteOpt.isPresent()) {
            Aula aulaExistente = aulaExistenteOpt.get();

            try {
                classUtil.copyProperties(aulaExistente, aulaModificada);
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar el aula", e);
            }

            return repo.save(aulaExistente);
        }

        return aulaModificada;
    }

    public Aula createAula(Aula aula) {
        return repo.save(aula);
    }

    public void deleteAula(Long id) {
        repo.deleteById(id);
    }
}
