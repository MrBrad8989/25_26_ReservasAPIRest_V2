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

**Frontend incluido (estático)**

- He añadido un frontend estático mínimo en `src/main/resources/static`:
	- `index.html` — interfaz simple de autenticación y botones de acción.
	- `app.js` — lógica JS para login y consulta a `GET /api/aulas` usando JWT.
	- `styles.css` — estilos básicos.

- Cómo usarlo:

	1. Arranca la aplicación Spring Boot:

	```bash
	./mvnw spring-boot:run
	```

	2. Abre `http://localhost:8080/` en el navegador.
	3. Inicia sesión con un usuario válido (`/api/auth/login`). Si la respuesta contiene el token, el frontend lo guardará en `localStorage`.
	4. Haz clic en "Listar aulas" para consultar `GET /api/aulas` con el token en la cabecera `Authorization: Bearer <token>`.

- Notas:
	- Este frontend es una base ligera para pruebas y demostraciones. Si prefieres un SPA con React/Vue/Angular, puedo generar el scaffold y conectar la build con `frontend-maven-plugin`.

**Frontend ampliado (novedades)**

- He ampliado el frontend estático con vistas CRUD y mejoras visuales. Archivos añadidos/actualizados en `src/main/resources/static`:
	- `index.html` — interfaz completa con navegación y formularios CRUD para Aulas, Horarios, Reservas y Usuarios.
	- `app.js` — lógica JS para autenticación (login), gestión de token en `localStorage`, CRUD para recursos y navegación entre vistas. Incluye un botón "Modo demo" que almacena un token simulado (`DEMO`) para pruebas rápidas.
	- `styles.css` — rediseño visual moderno y responsive.
	- `package.json` — placeholder mínimo para que `frontend-maven-plugin` no falle (`npm install` / `npm run build`).

**Cómo usar el frontend ampliado**

1. Asegúrate de tener JDK 17 y Maven (usa el wrapper incluido). Si tu entorno no tiene Java 17, instala/configura OpenJDK 17 antes de compilar.

2. Arranca la aplicación (desde la raíz del proyecto):

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

3. Abre en el navegador:

```
http://localhost:8080/
```

4. Prueba el frontend:
	- Usa "Modo demo" para pruebas rápidas (almacena `DEMO` como token en `localStorage`). Algunas llamadas protegidas pueden requerir un token válido emitido por `/api/auth/login`.
	- Navega entre las pestañas: `Aulas`, `Horarios`, `Reservas`, `Usuarios`.
	- Crea/edita/borra recursos (nota: para crear/editar/borrar aulas y horarios puede ser necesario un usuario con rol `ADMIN`).

**Notas y soluciones a problemas comunes**

- Si al ejecutar `./mvnw spring-boot:run` ves errores relacionados con `npm` (frontend-maven-plugin), ya se añadió un `package.json` mínimo en `src/main/resources/static` para evitar fallos de `npm install`.
- Si aparece el error `Fatal error compiling: error: release version 17 not supported`, asegúrate de ejecutar Maven con JDK 17. Comprueba con:

```bash
java -version
javac -version
mvn -v
```

- Si ves problemas de codificación al filtrar recursos (ej.: `MalformedInputException`), se añadió en `pom.xml`:

```xml
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
```

**Archivos clave añadidos**

- `src/main/resources/static/index.html` — nueva UI con navegación y formularios.
- `src/main/resources/static/app.js` — lógica de frontend (login, CRUD, navegación, demo mode).
- `src/main/resources/static/styles.css` — estilos mejorados.
- `src/main/resources/static/package.json` — placeholder para npm.

---
Si quieres, puedo ahora:
- Añadir iconos y mensajes de éxito/fracaso en la UI.
- Integrar una librería CSS (Tailwind/Bootstrap) o transformar esto en SPA con React + Vite y configurar `frontend-maven-plugin` para compilar la build.


