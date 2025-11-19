package com.reservas.reservasapirest.entities;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private String motivo;
    private Integer numeroAsistentes;

    // @CreationTimestamp
    private LocalDate fechaCreacion;

    @ManyToOne
    @JsonIgnoreProperties({"reservas"})
    private Aula aula;

    @ManyToOne
    @JsonIgnoreProperties({"reservas"})
    private Horario horario;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @PrePersist
    public void prePersist(){
        if(this.fechaCreacion == null) this.fechaCreacion = LocalDate.now();
    }

}
