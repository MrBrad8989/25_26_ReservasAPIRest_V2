# ReservasAPIRest

Proyecto REST API para gestionar reservas de aulas, creado con Spring Boot y Maven.

**Descripción breve**
- **Propósito:** API backend para crear, listar y gestionar aulas, horarios y reservas, con autenticación JWT.
- **Stack:** Java 17, Spring Boot (Web, Data JPA, Security), Maven, H2/Postgres/MySQL, JJWT para tokens JWT.

**Características**
- **Autenticación:** Inicio de sesión y registro, protección de endpoints con JWT.
- **Gestión de recursos:** CRUD para `Aula`, `Horario`, `Reserva` y `Usuario`.
- **Persistencia flexible:** Soporta H2 (desarrollo), PostgreSQL y MySQL.
- **Validaciones y manejo de errores:** Spring Validation y controlador global de excepciones.

**Tecnologías**
- **Lenguaje:** Java 17 (`<java.version>17</java.version>` en `pom.xml`).
- **Frameworks:** Spring Boot (Web, Data JPA, Security, Validation).
- **Token JWT:** `io.jsonwebtoken:jjwt`.
- **Base de datos:** H2 (runtime), PostgreSQL y MySQL opcionales.
- **Build:** Maven (`./mvnw`).

**Estructura del proyecto (resumen)**
- **Paquetes principales:**
	- `com.reservas.reservasapirest.controllers` — controladores REST (`AulaController`, `AutentificacionController`, `HorarioController`, `ReservaController`, `UsuarioController`).
	- `com.reservas.reservasapirest.entities` — entidades JPA (`Aula`, `Horario`, `Reserva`, `Usuario`).
	- `com.reservas.reservasapirest.repositories` — interfaces de acceso a datos (`AulaRepo`, `HorarioRepo`, `ReservaRepo`, `UsuarioRepo`).
	- `com.reservas.reservasapirest.services` — lógica de negocio y gestión de JWT (`AulaService`, `HorarioService`, `ReservaService`, `UsuarioService`, `JwtService`).
	- `com.reservas.reservasapirest.config` y `utils` — configuración de seguridad, filtros y utilidades.
- **Archivos importantes:**
	- `src/main/resources/application.properties` — configuración de la aplicación (datasource, jwt, etc.).
	- `pom.xml` — dependencias y plugins (incluye `frontend-maven-plugin` si hay frontend estático).

**Requisitos**
- **JDK:** 17
- **Maven:** incluido en el proyecto con el wrapper (`./mvnw`).
- **Opcional:** PostgreSQL o MySQL si no se usa H2 en memoria.
- **Node/npm:** sólo si quieres usar la parte frontend configurada en `frontend-maven-plugin`.

**Configuración rápida**
- Copia/ajusta `src/main/resources/application.properties` para tu entorno (url, usuario, contraseña de BD, claves JWT).

**Ejecutar en modo desarrollo**
Usando el wrapper incluido (Linux/macOS):

```bash
./mvnw spring-boot:run
```

O empaquetar y ejecutar el JAR:

```bash
./mvnw package -DskipTests
java -jar target/ReservasAPIRest-0.0.1-SNAPSHOT.jar
```

**Ejecutar tests**

```bash
./mvnw test
```

**Endpoints principales (basado en controladores)**
- `POST /api/auth/register` — registrar usuario (dependiendo de `AutentificacionController`).
- `POST /api/auth/login` — iniciar sesión y recibir JWT.
- `GET|POST|PUT|DELETE /api/aulas` — gestionar aulas.
- `GET|POST|PUT|DELETE /api/horarios` — gestionar horarios.
- `GET|POST|PUT|DELETE /api/reservas` — gestionar reservas.
- `GET|POST|PUT|DELETE /api/usuarios` — gestionar usuarios (según permisos).

Nota: Las rutas exactas pueden variar; revisa los controladores en `src/main/java/com/reservas/reservasapirest/controllers`.

**Ejemplo de uso (curl)**

- Login y obtener token (ejemplo):

```bash
curl -X POST http://localhost:8080/api/auth/login \
	-H "Content-Type: application/json" \
	-d '{"username":"usuario","password":"pass"}'
```

- Llamada a endpoint protegido con token:

```bash
curl -X GET http://localhost:8080/api/aulas \
	-H "Authorization: Bearer <TU_JWT>"
```

**Notas de desarrollo**
- El proyecto incluye `DataLoader` y `AulasComunes` para datos de ejemplo en arranque; revisa estos para entender datos iniciales.
- Seguridad: `SecurityConfig` y `JwtAuthenticationFilter` implementan la protección por JWT.

**Contribuir**
- Abre issues o pull requests. Mantén branch pequeño y pruebas verdes.

**Licencia**
- Añade la licencia que prefieras en el repositorio (aún no especificada en `pom.xml`).

Si quieres, puedo:
- Añadir ejemplos concretos de `application.properties` para H2/Postgres.
- Generar documentación Swagger/OpenAPI.
- Detallar los endpoints con ejemplos reales leyendo los controladores.

---
Archivo generado automáticamente: documentación base. Pídeme que lo personalice más si quieres.

