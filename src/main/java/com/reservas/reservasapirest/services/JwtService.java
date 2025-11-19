package com.reservas.reservasapirest.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders; // Importar Decoders
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}") // Asumo que tienes una propiedad para la expiración
    private long jwtExpiration;

    // Getter helper para la expiración en segundos (útil para cookies)
    public long getJwtExpirationSeconds() {
        return jwtExpiration / 1000;
    }

    /**
     * Extrae el nombre de usuario (email) del token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae una "claim" específica (información) del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT para un usuario.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con "claims" extra.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Usa la expiración
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma el token
                .compact();
    }

    /**
     * Valida si un token es correcto y pertenece al usuario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todas las "claims" del token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * --- ¡AQUÍ ESTÁ LA SOLUCIÓN! ---
     * Convierte la clave secreta (String) en una Clave (Key) criptográfica.
     * El error "Illegal base64 character: '-'" significa que tu secret key
     * está codificada en Base64URL, pero el código original probablemente usaba Decoders.BASE64.
     * Lo cambiamos a Decoders.BASE64URL.
     */
    private Key getSigningKey() {
        // Intentamos primero interpretar el secret como Base64URL/Base64.
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64URL.decode(jwtSecret);
        } catch (IllegalArgumentException e1) {
            try {
                // Si no es Base64URL válido, probamos con Base64 estándar
                keyBytes = Decoders.BASE64.decode(jwtSecret);
            } catch (IllegalArgumentException e2) {
                // Si no es Base64, usamos los bytes UTF-8 del secret
                keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            }
        }

        // JJWT requiere claves >= 256 bits (32 bytes) para HS256. Si la clave es más corta,
        // derivamos una clave de 256 bits usando SHA-256 del valor proporcionado.
        if (keyBytes.length < 32) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(keyBytes);
            } catch (NoSuchAlgorithmException ex) {
                // SHA-256 siempre debería estar disponible en la JVM; si no, lanzamos runtime
                throw new RuntimeException("SHA-256 algorithm not available", ex);
            }
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}