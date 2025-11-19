package com.reservas.reservasapirest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CambiarPasswordRequestDTO(
        @NotEmpty(message = "La contraseña antigua es obligatoria")
        String antiguaPassword,
        @NotEmpty(message = "La nueva contraseña es obligatoria y no puede estar vacía")
        @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
        String nuevaPassword
) {
}
