# ReservasAPIRest

Proyecto REST API para gestionar reservas de aulas, creado con Spring Boot y Maven, con interfaz web completa incluida.

**Inicio rápido**

```bash
# Clonar el repositorio
git clone <url-del-repo>
cd 25_26_ReservasAPIRest_V2

# Ejecutar la aplicación
./mvnw spring-boot:run

# Abrir en el navegador
# http://localhost:8080/
```

**Descripción breve**
- **Propósito:** API backend para crear, listar y gestionar aulas, horarios y reservas, con autenticación JWT e interfaz web incluida.
- **Stack:** Java 17, Spring Boot (Web, Data JPA, Security), Maven, H2/Postgres/MySQL, JJWT para tokens JWT, Frontend vanilla JavaScript.

**Características**
- **Autenticación:** Inicio de sesión y registro, protección de endpoints con JWT.
- **Gestión de recursos:** CRUD para `Aula`, `Horario`, `Reserva` y `Usuario`.
- **Persistencia flexible:** Soporta H2 (desarrollo), PostgreSQL y MySQL.
- **Validaciones y manejo de errores:** Spring Validation y controlador global de excepciones.
- **Interfaz web:** Frontend completo en vanilla JavaScript para gestionar todos los recursos sin necesidad de herramientas externas.

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

**Frontend incluido (interfaz web completa)**

El proyecto ahora incluye un frontend web completo desarrollado en vanilla JavaScript, ubicado en `src/main/resources/static`. Esta interfaz permite gestionar todos los recursos de la API de forma visual sin necesidad de usar herramientas como Postman o curl.

**Archivos del frontend:**
- `index.html` — Interfaz HTML5 con formularios y navegación entre secciones
- `app.js` — Lógica JavaScript para autenticación JWT y operaciones CRUD
- `styles.css` — Estilos modernos con diseño responsivo y gradientes

**Funcionalidades disponibles:**

1. **Autenticación**
   - Login con usuario y contraseña
   - Almacenamiento seguro del token JWT en `localStorage`
   - Modo demo para pruebas rápidas
   - Cierre de sesión

2. **Gestión de Aulas**
   - Listar todas las aulas disponibles
   - Crear nuevas aulas con capacidad y equipamiento
   - Editar aulas existentes
   - Eliminar aulas
   - Campos: nombre, capacidad, ordenadores (sí/no), número de ordenadores

3. **Gestión de Horarios**
   - Listar todos los horarios
   - Crear nuevos horarios con día y sesión
   - Eliminar horarios
   - Campos: día de la semana, sesión diaria, hora inicio, hora fin

4. **Gestión de Reservas**
   - Listar todas las reservas
   - Crear nuevas reservas seleccionando aula y horario
   - Eliminar reservas
   - Campos: fecha, motivo, número de asistentes, aula, horario

5. **Gestión de Usuarios**
   - Listar usuarios registrados
   - Crear nuevos usuarios (registro)
   - Eliminar usuarios
   - Asignar roles (PROFESOR, ADMIN)

**Cómo usar el frontend:**

1. Arranca la aplicación Spring Boot:

   ```bash
   ./mvnw spring-boot:run
   ```

2. Abre tu navegador en `http://localhost:8080/`

3. **Primera vez:** Inicia sesión con las credenciales por defecto (definidas en `DataLoader.java`) o usa el botón "Modo demo" para pruebas rápidas.

4. **Navegación:** Usa los botones en la barra de navegación para cambiar entre las diferentes secciones (Aulas, Horarios, Reservas, Usuarios).

5. **Operaciones:** Cada sección permite crear, listar y eliminar recursos. Los formularios se validan automáticamente.

6. **Token JWT:** El token se gestiona automáticamente. Todas las peticiones a la API incluyen el header `Authorization: Bearer <token>`.

**Características técnicas:**
- Interfaz 100% vanilla JavaScript (sin frameworks)
- Diseño responsivo que se adapta a móviles y tablets
- Manejo de errores con mensajes informativos
- Validación de formularios en cliente
- Actualización automática de listas después de cada operación
- Sistema de navegación por pestañas
- Estilos modernos con variables CSS y gradientes

**Notas:**
- Este frontend está optimizado para desarrollo y pruebas. Para producción, considera añadir validaciones adicionales y manejo de errores más robusto.
- Si prefieres migrar a un framework moderno (React, Vue, Angular), la estructura actual facilita la integración con `frontend-maven-plugin`.

