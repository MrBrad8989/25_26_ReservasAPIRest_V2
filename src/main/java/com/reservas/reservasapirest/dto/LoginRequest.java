package com.reservas.reservasapirest.dto;

// Esta clase se usa para recibir el JSON del login
public class LoginRequest {
    private String email;
    private String password;

    // Getters y Setters (necesarios para que Spring Jackson deserialice el JSON)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}