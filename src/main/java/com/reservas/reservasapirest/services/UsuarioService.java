package com.reservas.reservasapirest.services;

import com.reservas.reservasapirest.dto.CambiarPasswordRequestDTO;
import com.reservas.reservasapirest.dto.UsuarioResponseDTO;
import com.reservas.reservasapirest.entities.Usuario;
import com.reservas.reservasapirest.repositories.UsuarioRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepo usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }

    public UsuarioResponseDTO obtenerPerfilActual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return UsuarioResponseDTO.fromUsuario(usuario);
    }

    public void cambiarPassword(CambiarPasswordRequestDTO requestDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        //Verificar que la contraseña antigua coincida
        if (!passwordEncoder.matches(requestDTO.antiguaPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña antigua es incorrecta");
        }
        // Verificar que la nueva contraseña sea diferente de la antigua
        if (passwordEncoder.matches(requestDTO.nuevaPassword(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña debe ser diferente de la antigua");
        }

        // Codificar y actualizar la nueva contraseña
        usuario.setPassword(passwordEncoder.encode(requestDTO.nuevaPassword()));
        usuarioRepository.save(usuario);
    }

    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::fromUsuario) // Convierte Usuario a DTO
                .collect(Collectors.toList());
    }
}
