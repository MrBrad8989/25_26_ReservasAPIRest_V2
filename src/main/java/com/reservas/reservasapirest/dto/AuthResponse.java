package com.reservas.reservasapirest.dto;

// Esta clase se usa para enviar la respuesta JSON (el token) al frontend
public class AuthResponse {
    private String token;
    private String mensaje;

    // Constructor que usamos en el AutentificacionController
    public AuthResponse(String token, String mensaje) {
        this.token = token;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}