package com.reservas.reservasapirest.dto;

import com.reservas.reservasapirest.entities.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String email,
        Usuario.Role role
) {
    public static UsuarioResponseDTO fromUsuario(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}
