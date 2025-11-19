package com.reservas.reservasapirest.entities;

import jakarta.persistence.*;

import java.util.List;
import com.reservas.reservasapirest.entities.Reserva;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"reservas"})
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer capacidad;
    private Boolean esAulaOrdenadores;
    private Integer numeroOrdenadores;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "aula")
    private List<Reserva> reservas;

    // Constructor sin el campo 'id' y 'reservas'
    public Aula(String nombre, Integer capacidad, Boolean esAulaOrdenadores, Integer numeroOrdenadores) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.esAulaOrdenadores = esAulaOrdenadores;
        this.numeroOrdenadores = numeroOrdenadores;
    }
}

