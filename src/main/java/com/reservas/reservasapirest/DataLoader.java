package com.reservas.reservasapirest;

import com.reservas.reservasapirest.entities.Aula;
import com.reservas.reservasapirest.entities.Horario;
import com.reservas.reservasapirest.entities.Usuario;
import com.reservas.reservasapirest.repositories.AulaRepo;
import com.reservas.reservasapirest.repositories.HorarioRepo;
import com.reservas.reservasapirest.repositories.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Esta clase se ejecuta al iniciar la aplicación.
 * Se utiliza para precargar datos en la base de datos si está vacía.
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private AulaRepo aulaRepo;

    @Autowired
    private HorarioRepo horarioRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectado para encriptar contraseñas

    @Override
    public void run(String... args) throws Exception {

        // Comprobar si ya hay datos para no duplicar
        if (usuarioRepo.count() == 0 && aulaRepo.count() == 0 && horarioRepo.count() == 0) {
            System.out.println("Base de datos vacía, cargando datos iniciales...");
            loadInitialData();
        } else {
            System.out.println("La base de datos ya contiene datos. No se carga el DataLoader.");
        }
    }

    /**
     * Método privado para cargar todos los datos iniciales
     */
    private void loadInitialData() {

        // --- 1. Crear Aulas ---
        // Usamos el constructor: public Aula(String nombre, Integer capacidad, Boolean esAulaOrdenadores, Integer numeroOrdenadores)
        Aula aula1 = new Aula("Aula 101", 30, false, 0);
        Aula aula2 = new Aula("Aula 102", 25, false, 0);
        Aula aula3 = new Aula("Laboratorio A (PCs)", 20, true, 20);
        Aula aula4 = new Aula("Salón de Actos", 150, true, 1); // 1 PC para presentaciones
        aulaRepo.saveAll(Arrays.asList(aula1, aula2, aula3, aula4));


        // --- 2. Crear Horarios (Tramos) ---
        // Usamos el constructor: public Horario (String diaSemana, Integer sesionDiaria, LocalTime horaInicio, LocalTime horaFin)
        List<Horario> horarios = new ArrayList<>();
        List<String> diasSemana = Arrays.asList("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES");

        // Definición de las 7 sesiones del día
        LocalTime[] horasInicio = {
                LocalTime.of(8, 15), LocalTime.of(9, 10), LocalTime.of(10, 5),
                LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 25),
                LocalTime.of(13, 20)
        };
        LocalTime[] horasFin = {
                LocalTime.of(9, 10), LocalTime.of(10, 5), LocalTime.of(11, 0),
                LocalTime.of(11, 30), LocalTime.of(12, 25), LocalTime.of(13, 20),
                LocalTime.of(14, 15)
        };

        // Bucle para crear los horarios de Lunes a Viernes
        for (String dia : diasSemana) {
            for (int i = 0; i < horasInicio.length; i++) {
                // (String diaSemana, Integer sesionDiaria, LocalTime horaInicio, LocalTime horaFin)
                horarios.add(new Horario(
                        dia,
                        i + 1, // sesionDiaria (1, 2, 3... 7)
                        horasInicio[i],
                        horasFin[i]
                ));
            }
        }
        horarioRepo.saveAll(horarios);


        // --- 3. Crear Usuarios ---
        Usuario admin = Usuario.builder()
                .nombre("Administrador")
                .email("admin@instituto.com")
                .password(passwordEncoder.encode("admin")) // Contraseña encriptada
                .role(Usuario.Role.ROLE_ADMIN)
                .build();

        Usuario profe1 = Usuario.builder()
                .nombre("Profesor Uno")
                .email("profe1@instituto.com")
                .password(passwordEncoder.encode("profe1")) // Contraseña encriptada
                .role(Usuario.Role.ROLE_PROFESOR)
                .build();

        Usuario profe2 = Usuario.builder()
                .nombre("Profesor Dos")
                .email("profe2@instituto.com")
                .password(passwordEncoder.encode("profe2")) // Contraseña encriptada
                .role(Usuario.Role.ROLE_PROFESOR)
                .build();

        usuarioRepo.saveAll(List.of(admin, profe1, profe2));

        System.out.println("Datos iniciales cargados correctamente.");
    }
}