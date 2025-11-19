package com.reservas.reservasapirest.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservaRequestDTO(

        @NotNull(message = "La fecha es obligatoria")
        @Future(message = "No se permiten reservas en el pasado") // [cite: 41]
        LocalDate fecha,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,

        @NotNull(message = "El número de asistentes es obligatorio")
        @Min(value = 1, message = "Debe haber al menos 1 asistente")
        Integer numeroAsistentes,

        @NotNull(message = "El ID del aula es obligatorio")
        Long aulaId,

        @NotNull(message = "El ID del horario es obligatorio")
        Long horarioId
) {}