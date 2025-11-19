package com.reservas.reservasapirest.config;

import com.reservas.reservasapirest.services.JwtService;
import com.reservas.reservasapirest.services.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    // Inyección de dependencias por constructor
    public JwtAuthenticationFilter(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    // Metodo principal del filtro que se ejecuta una vez por solicitud
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener el encabezado 'Authorization'
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Log corto para depuración: ruta y si tiene Authorization
        System.out.println("[JwtFilter] " + request.getMethod() + " " + request.getRequestURI() + " Authorization-present=" + (authHeader!=null));

        // 2. Si no hay encabezado o no empieza con "Bearer ", pasar al siguiente filtro
        String tokenCandidate = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenCandidate = authHeader.substring(7);
        } else {
            // Intentar leer cookie 'jwt' si no hay header
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                tokenCandidate = Arrays.stream(cookies)
                        .filter(c -> "jwt".equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }
        }

        if (tokenCandidate == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Token final a usar
        jwt = tokenCandidate;

        try {
            // 4. Extraer el email (username) del token
            userEmail = jwtService.extractUsername(jwt);
            System.out.println("[JwtFilter] extracted userEmail=" + userEmail);

            // 5. Si tenemos email y el usuario no está autenticado en el contexto de seguridad
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Cargar los detalles del usuario desde la base de datos
                UserDetails userDetails = this.usuarioService.loadUserByUsername(userEmail);
                System.out.println("[JwtFilter] loaded userDetails.username=" + userDetails.getUsername());
                System.out.println("[JwtFilter] userDetails.authorities=" + userDetails.getAuthorities());

                // 7. Validar el token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    System.out.println("[JwtFilter] token is valid for user=" + userDetails.getUsername());
                    // 8. Si es válido, crear un token de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // No usamos credenciales (password) aquí
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 9. Establecer el usuario como autenticado en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[JwtFilter] SecurityContext authentication set: " + SecurityContextHolder.getContext().getAuthentication());
                } else {
                    System.out.println("[JwtFilter] token is NOT valid for user=" + userDetails.getUsername());
                    // No autenticamos
                }
            }

            // 10. Pasar al siguiente filtro
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Si hay cualquier error al parsear el token, etc.,
            // simplemente no autenticamos y pasamos al siguiente filtro.
            // Spring Security se encargará de devolver el 401 más adelante.
            System.err.println("Error al procesar el token JWT: " + e.getMessage());
            filterChain.doFilter(request, response);
        }
    }
}