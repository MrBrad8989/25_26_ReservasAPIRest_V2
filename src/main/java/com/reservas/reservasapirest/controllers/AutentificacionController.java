package com.reservas.reservasapirest.controllers;

import com.reservas.reservasapirest.dto.AuthResponse;
import com.reservas.reservasapirest.dto.LoginRequest;
import com.reservas.reservasapirest.dto.RegisterRequest;
import com.reservas.reservasapirest.entities.Usuario;
import com.reservas.reservasapirest.repositories.UsuarioRepo;
import com.reservas.reservasapirest.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AutentificacionController {

    // Inyectamos todas las dependencias necesarias
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepo usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutentificacionController(AuthenticationManager authenticationManager,
                                     UsuarioRepo usuarioRepo,
                                     PasswordEncoder passwordEncoder,
                                     JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint para iniciar sesión.
     * Recibe credenciales y devuelve un token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            // Autenticar al usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            // Si la autenticación falla (ej. contraseña incorrecta)
            return ResponseEntity.status(401).body(new AuthResponse(null, "Credenciales inválidas"));
        }

        // Si la autenticación fue exitosa, buscar al usuario para generar el token
        UserDetails userDetails = usuarioRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de autenticación exitosa"));

        // Generar el token
        String token = jwtService.generateToken(userDetails);

        // Guardar token en cookie HttpOnly para que el navegador lo envíe automáticamente
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .path("/")
                .maxAge(jwtService.getJwtExpirationSeconds())
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // Devolver el token
        return ResponseEntity.ok(new AuthResponse(token, "Login exitoso"));
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * Recibe los datos del usuario, lo crea, y DEVUELVE UN TOKEN JWT.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request, HttpServletResponse response) {

        // 1. Comprobar si el email ya existe
        Optional<Usuario> usuarioExistente = usuarioRepo.findByEmail(request.getEmail());
        if (usuarioExistente.isPresent()) {
            // Devolver 400 Bad Request si el email ya está en uso
            return ResponseEntity
                    .badRequest()
                    .body(new AuthResponse(null, "El correo electrónico ya está registrado"));
        }

        // 2. Crear el nuevo usuario
        Usuario nuevoUsuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // ¡Encriptar la contraseña!
                .role(Usuario.Role.ROLE_PROFESOR) // Rol por defecto
                .build();

        // 3. Guardar el usuario en la base de datos
        Usuario usuario =usuarioRepo.save(nuevoUsuario);

        // 4. Generar un token para el nuevo usuario
        // (UserDetails es implementado por tu entidad Usuario)
        String token = jwtService.generateToken(usuario);

        // Guardar token en cookie HttpOnly para que el navegador lo envíe automáticamente
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .path("/")
                .maxAge(jwtService.getJwtExpirationSeconds())
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // 5. Devolver el token
        return ResponseEntity.ok(new AuthResponse(token, "Registro exitoso"));
    }
    /**
     * Endpoint para cerrar sesión.
     * Invalida la cookie JWT del navegador enviando una cookie
     * con el mismo nombre y path, pero con maxAge = 0.
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logoutUser(HttpServletResponse response) {

        // 1. Crear una cookie que expire inmediatamente
        // DEBE coincidir con las propiedades de la cookie original (path, sameSite, httpOnly)
        ResponseCookie cookie = ResponseCookie.from("jwt", "") // Valor vacío
                .httpOnly(true)
                .path("/") // Coincide con el path de la cookie de login
                .maxAge(0) // ¡Expira inmediatamente!
                .sameSite("Lax") // Coincide con la cookie de login
                .build();

        // 2. Añadir la cookie de expiración a la respuesta
        response.addHeader("Set-Cookie", cookie.toString());

        // 3. (Opcional pero recomendado) Limpiar el contexto de seguridad de Spring
        SecurityContextHolder.clearContext();

        // 4. Devolver respuesta
        return ResponseEntity.ok(new AuthResponse("Vaciado", "Logout exitoso"));
    }
}