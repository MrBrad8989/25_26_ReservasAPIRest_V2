package com.reservas.reservasapirest.entities;

import jakarta.persistence.*;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalTime;
import java.util.List;
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diaSemana;
    private Integer sesionDiaria;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "horario")
    @JsonIgnoreProperties({"horario"})
    private List<Reserva> reservas;

    // Constructor sin el campo 'id' y 'reservas'
    public Horario (String diaSemana, Integer sesionDiaria, LocalTime horaInicio, LocalTime horaFin) {
        this.diaSemana = diaSemana;
        this.sesionDiaria = sesionDiaria;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
}

