# ─── Etapa 1: Compilar con Maven ───────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar pom.xml primero para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─── Etapa 2: Imagen final ligera ──────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar solo el JAR de la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Variables de entorno con valores por defecto (se sobreescriben en Compose)
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080

EXPOSE 8080

# Arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
