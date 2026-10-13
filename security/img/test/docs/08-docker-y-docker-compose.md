# 08 — Contenedorización con Docker y Docker Compose

## 1. Arquitectura de Construcción Multietapa (`Dockerfile`)

El archivo `Dockerfile` implementa una estrategia de construcción multi-stage optimizada para producción:

```dockerfile
# Etapa 1: Compilación y empaquetado con JDK completo
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# Etapa 2: Imagen final de ejecución liviana (JRE Alpine)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Ventajas de esta configuración:
1. **Reducción del tamaño de la imagen:** No traslada herramientas de compilación, código fuente ni repositorio `.m2` a la imagen final.
2. **Seguridad reforzada:** Se basa en una imagen mínima Alpine con superficie de ataque reducida y sin utilidades innecesarias.
3. **Aislamiento de secretos:** La imagen final solo contiene el artefacto `app.jar` y recibe las variables de entorno en tiempo de ejecución.

---

## 2. Orquestación Local con Docker Compose (`compose.yaml`)

El archivo `compose.yaml` coordina los dos servicios requeridos para una réplica completa del entorno productivo:

```yaml
services:
  mongodb:
    image: mongo:7
    container_name: mongo-parcial
    restart: unless-stopped
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_DATABASE: dosw_parcial
    volumes:
      - mongo-data:/data/db
    healthcheck:
      test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: dosw-parcial-app
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      PORT: 8080
      MONGODB_URI: mongodb://mongodb:27017/dosw_parcial
      JWT_SECRET: ${JWT_SECRET:-404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
    depends_on:
      mongodb:
        condition: service_healthy

volumes:
  mongo-data:
    driver: local
```

### Aspectos Clave de Red y Persistencia:
- **Resolución DNS interna:** El contenedor de Spring Boot se comunica con la base de datos mediante el nombre del servicio `mongodb://mongodb:27017/dosw_parcial` dentro de la red interna creada automáticamente por Docker Compose. Nunca apunta a `localhost` dentro de un contenedor.
- **Healthcheck condicional:** La aplicación Java no intenta iniciar hasta que MongoDB responde satisfactoriamente al comando de ping de `mongosh`.
- **Persistencia en volumen:** Los datos de MongoDB persisten en el volumen local `mongo-data` incluso si los contenedores son recreados.

---

## 3. Comandos de Gestión de Contenedores

### Iniciar la plataforma completa en segundo plano:
```bash
docker compose up -d --build
```

### Consultar estado de los contenedores:
```bash
docker compose ps
```

### Visualizar logs en tiempo real:
```bash
docker compose logs -f app
```

### Detener los servicios:
```bash
docker compose down
```

### Detener y eliminar volúmenes (limpieza total):
```bash
docker compose down -v
```
