# --- Etapa 1: Construcción (Build) ---
# Usamos una imagen que tiene Maven y Java 17 instalados
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos todo el código del proyecto al contenedor
COPY . .

# Compilamos y empaquetamos (esto instalará Node.js y construirá el frontend también)
RUN mvn clean package -DskipTests

# --- Etapa 2: Ejecución (Run) ---
# Usamos una imagen ligera de Java 17 para ejecutar la app
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiamos el JAR generado en la etapa anterior
# Nota: El nombre coincide con el artifactId y version de tu pom.xml
COPY --from=build /app/target/ReservasAPIRest-0.0.1-SNAPSHOT.jar app.jar

# Informamos a Docker que la app usa el puerto 8081
EXPOSE 8081

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
