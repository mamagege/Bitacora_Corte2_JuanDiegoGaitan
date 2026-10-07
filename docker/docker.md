# 🐳 Semana 10 · DOWS · Dockerización (Guía Turtwig S10)

Bienvenido a la etapa de empaquetado y distribucion. 
Nuestra API de Restaurante ya es perfecta y funciona con PostgreSQL, MongoDB, Spring Security y JWT en localhost. Sin embargo, en el mundo real, el terrible pretexto de **"En mi maquina si funciona"** esta prohibido. 
**Docker** soluciona este problema empaquetando el codigo, la maquina virtual de Java y el sistema operativo en una sola caja que corre exactamente igual en tu computadora, en el computador de un compañero o en los servidores mundiales de Amazon/Azure.

---

## 📦 PASO 1: Fundamentos de Docker

Antes de escribir un Dockerfile, debes dominar los tres conceptos universales que gobiernan los contenedores.

### 1. La Imagen (El Molde Inmutable) 💿
Una Imagen es una plantilla de solo-lectura que contiene todo lo necesario para correr la aplicacion:
- El Sistema Operativo (Ej: Alpine Linux).
- El Runtime (Ej: Java 17).
- Tu codigo compilado (.jar).
> **Comandos clave:** docker build (crear), docker images (listar).

### 2. El Contenedor (La Instancia Viva) 🏃
Un Contenedor es la imagen traida a la vida (instanciada). Piensa en la Imagen como una Clase de Java y el Contenedor como un Objeto instanciado con 
ew. Puedes correr multiples contenedores (puertos distintos) desde una misma imagen.
> **Comandos clave:** docker run (iniciar), docker ps (ver activos), docker stop (detener).

### 3. El Registry (La Nube de Moldes) 🗂️
Es un repositorio mundial (o privado empresarial) donde las imagenes se suben para que cualquier otra maquina pueda descargarlas.
- **Públicos:** Docker Hub.
- **Privados:** Azure Container Registry (ACR), AWS Elastic Container Registry (ECR).
> **Comandos clave:** docker pull (descargar imagen), docker push (subir imagen).

---

## 🗺️ El Flujo Arquitectónico hacia Produccion

Asi es como fluye la vida de tu aplicacion Restaurante a partir de hoy:

1. **Codigo Fuente + Dockerfile** (src/ + Dockerfile)
2. **Construccion Local** (docker build -> Imagen Local)
3. **Distribucion** (docker push -> Docker Hub)
4. **Despliegue** (El Servidor remoto hace docker pull y docker run)
5. **Ejecucion Multi-Contenedor** (El Contenedor de la API se conecta al Contenedor de PostgreSQL y al Contenedor de MongoDB, todos corriendo en armonía gracias a Docker Compose).

---
*Ambiente Validado: Docker version 29.8.0 y Docker Compose version v5.5.1 estan operando en tu maquina exitosamente.*

---

## 📄 PASO 2: Empaquetar la API (Dockerfile y Configuración)

El **Dockerfile** es la receta de cocina que Docker lee paso a paso para construir la imagen de nuestra API. Para hacerlo de la manera más profesional y optimizada, hemos implementado un **Build Multietapa**.

### 1. El Build Multietapa (Dockerfile)
En lugar de empaquetar todo el código fuente y las pesadas herramientas de Maven en la imagen de producción, usamos dos etapas. La Etapa 1 compila el .jar, y la Etapa 2 simplemente copia el .jar resultante en un sistema operativo mínimo de Java (lpine). ¡El tamaño de la imagen se reduce drásticamente!

Este es el contenido de nuestro Dockerfile en la raíz del proyecto:
`dockerfile
# ─── Etapa 1: Compilar con Maven ───────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar pom.xml primero para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiar codigo fuente y compilar
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
`

### 2. El Guardián del Peso (.dockerignore)
Al igual que .gitignore, el .dockerignore le dice a Docker qué archivos de tu computador **NO** debe copiar al construir la imagen. Esto evita meter basura o archivos pesados innecesarios. Nuestro archivo contiene:
`	ext
.git
.gitignore
.mvn
target/
*.md
*.p12
.env
`

### 3. Configuración Específica (application-docker.yml)
Dado que nuestra API va a correr dentro de un contenedor, ya no puede conectarse a localhost:5432 para buscar PostgreSQL (porque su "localhost" es el interior del propio contenedor). Por ello, hemos creado el archivo src/main/resources/application-docker.yml que utiliza resolución por nombres de red de Docker (ej: buscará a Postgres en el host llamado postgres en lugar de localhost).

`yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:postgres}:${DB_PORT:5432}/${DB_NAME:restaurante}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
`
> 💡 **Nota sobre las Variables:** La sintaxis ${VARIABLE:valor_por_defecto} permite que la API pueda arrancar usando el valor por defecto si nosotros (o el administrador del servidor) no le inyectamos explícitamente esa variable de entorno. ¡Brillante flexibilidad!

---

## 🗄️ PASO 3: Base de Datos en Contenedor (Adiós Instalaciones Locales)

Uno de los superpoderes absolutos de Docker es que ya **nunca mas tendras que instalar y configurar motores de base de datos** en tu sistema operativo nativo. Ni Postgres, ni Mongo ensucian el registro de Windows ni los procesos de tu Mac. 

Los descargamos, los encendemos y los apagamos a demanda mediante una Imagen Oficial preconfigurada.

### 🐘 1. Levantando PostgreSQL
Para tener una Base de Datos relacional lista para escuchar tu API, abre una terminal y ejecuta:

docker run -d `
--name postgres-restaurante `
-e POSTGRES_DB=restaurante `
-e POSTGRES_USER=postgres `
-e POSTGRES_PASSWORD=postgres `
-p 5432:5432 `
-v postgres-data:/var/lib/postgresql/data `
postgres:16-alpine

*Verificacion:* Si ejecutas docker logs postgres-restaurante, verás el texto *"database system is ready to accept connections"*.

### 🍃 2. Levantando MongoDB
Si nuestro restaurante va a usar la arquitectura hibrida (Eventos/Logs No-Relacionales), tambien le daremos vida a Mongo:

docker run -d `
--name mongo-restaurante `
-e MONGO_INITDB_ROOT_USERNAME=admin `
-e MONGO_INITDB_ROOT_PASSWORD=admin `
-e MONGO_INITDB_DATABASE=restaurante `
-p 27017:27017 `
-v mongo-data:/data/db `
mongo:7


docker ps

docker logs postgres-restaurante

.\run-db.ps1

### 💡 El Secreto: Los Volúmenes (-v)
¿Qué pasa si reinicias tu computador o borras el contenedor? ¿Se pierde la base de datos de tu Restaurante?
La respuesta es **NO**, gracias al parametro de **Volumen** (-v postgres-data:/ruta/interna). 
Un Volumen es un disco duro virtual que Docker crea y guarda a salvo en tu computadora. Cuando el contenedor muere, el disco duro sobrevive. Cuando vuelves a crear un contenedor y le enchufas ese mismo volumen, **¡Los datos del restaurante seguiran intactos!**

1. Solo Apagar (Pausar temporalmente) ⏸️
Si ya no vas a trabajar hoy y quieres que las bases de datos dejen de consumir memoria en tu computador, pero quieres que los contenedores sigan existiendo para prenderlos mañana rápido:

bash
docker stop postgres-restaurante mongo-restaurante
(Para volver a prenderlos mañana solo escribes: docker start postgres-restaurante mongo-restaurante).

2. Destruir los contenedores (Limpieza total) 🗑️
Si quieres borrar los contenedores por completo de tu sistema (quizás te equivocaste en algún puerto o variable y quieres volver a crearlos desde cero con el script run-db.ps1):

bash
docker rm -f postgres-restaurante mongo-restaurante
(El -f significa "Force", por lo que los apaga y los borra en un solo paso).

💡 Nota de tranquilidad: ¡Incluso si haces esto, tus datos no se borran! Gracias a que creamos Volúmenes (postgres-data y mongo-data), la información sigue a salvo en tu disco duro esperando a que vuelvas a crear un nuevo contenedor.

3. Apagar Docker por completo 🐳
Si no quieres que el motor de Docker (la ballena) siga corriendo en tu barra de tareas consumiendo recursos de Windows:

Ve a la barra de tareas en la esquina inferior derecha.
Haz clic derecho sobre el icono de la ballenita de Docker.
Selecciona "Quit Docker Desktop".
¡Guarda estos comandos! Son el pan de cada día de un desarrollador backend para administrar sus servicios locales.


---

## 🔧 PASO 4: Orquestación Total (Docker Compose)

En la industria, nadie levanta contenedores de bases de datos uno por uno con docker run y se cruza los dedos para que se comuniquen bien. Utilizamos **Docker Compose**: un archivo declarativo donde listamos todas las piezas de nuestro sistema (API, Postgres, Mongo) y dejamos que Docker construya una red interna para enlazarlas.

### El Archivo Maestro (docker-compose.yml)
En la raíz del proyecto hemos creado este archivo que reemplaza por completo a todos los comandos individuales:

`yaml
version: '3.9'

services:

  api:
    build: .                        # Ordena construir la Imagen usando tu Dockerfile
    container_name: restaurante-api
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: restaurante
      DB_USER: postgres
      DB_PASSWORD: postgres
      MONGO_HOST: mongo
      MONGO_PORT: 27017
      MONGO_DB: restaurante
      JWT_SECRET: ${JWT_SECRET}   # Viene del archivo .env oculto
    depends_on:
      postgres:
        condition: service_healthy
      mongo:
        condition: service_started
    networks:
      - restaurante-net

  postgres:
    image: postgres:16-alpine
    container_name: restaurante-postgres
    environment:
      POSTGRES_DB: restaurante
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - restaurante-net

  mongo:
    image: mongo:7
    container_name: restaurante-mongo
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: admin
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db
    networks:
      - restaurante-net

volumes:
  postgres-data:
  mongo-data:

networks:
  restaurante-net:
    driver: bridge
`

### Seguridad de Secretos (.env)
Nunca debes guardar llaves JWT o contraseñas de bases de datos de producción dentro del docker-compose.yml ni subirlas a Github. Por ello, creamos el archivo .env en la raíz del proyecto:
`properties
JWT_SECRET=TXkgc3VwZXIgc2VjcmV0IGtleSBwYXJhIGVsIHJlc3RhdXJhbnRl
SSL_KEYSTORE_PASSWORD=miContraseña123
`
Y nos hemos asegurado de agregarlo a .gitignore. En su lugar, los demás desarrolladores pueden usar el archivo .env.example para saber qué variables requiere el proyecto.

---

## ⌨️ PASO 5: Hoja de Trucos (Cheat Sheet) de Comandos

Con la arquitectura construida, la verdadera habilidad está en controlar los contenedores. Aquí tienes tu navaja suiza de comandos obligatorios para el día a día.

### Comandos Nativos de Docker
| Comando | Qué hace |
| :--- | :--- |
| docker build -t restaurante-api . | Construye la imagen leyendo tu Dockerfile y la etiqueta como la más reciente (*latest*). |
| docker build -t restaurante-api:1.0 . | Construye la imagen con una versión específica (Útil para revertir versiones). |
| docker images | Lista todos tus "moldes" descargados o creados. |
| docker run -d -p 8080:8080 restaurante-api | Enciende tu API y la esconde en segundo plano (-d *detached*). |
| docker run -it restaurante-api sh | Corre el contenedor en modo interactivo abriendo una terminal por dentro. Magia pura para hacer *debug*. |
| docker ps | Lista qué contenedores están vivos ahora mismo. |
| docker ps -a | Muestra el cementerio: todos los contenedores incluso los que se detuvieron por un error o manualmente. |
| docker logs restaurante-api | Lee la bitácora histórica de la terminal del contenedor. |
| docker logs -f restaurante-api | Pega tus ojos a la consola y te muestra los logs llegando **en tiempo real**. |
| docker exec -it restaurante-api sh | El contenedor ya está vivo, pero tú te metes a la fuerza por una consola (*SSH style*). |
| docker stop restaurante-api | Le dice al contenedor de forma amable que se apague y guarde sus variables en memoria temporal. |
| docker rm restaurante-api | Lo destruye permanentemente (Tiene que estar apagado primero). |
| docker rmi restaurante-api | Destruye el **molde** (la Imagen) para ahorrar espacio en tu disco duro. |
| docker volume ls | Descubre todos los discos duros persistentes. |
| docker system prune | *El botón de pánico.* Destruye todas las redes, contenedores e imágenes sin uso para liberar Gigabytes. |

### Comandos Maestros de Docker Compose
Dado que ya hicimos el *Paso 4*, tu día a día transcurrirá realmente utilizando este segundo bloque:

| Comando | Qué hace |
| :--- | :--- |
| docker compose up | Inicia Postgres, Mongo y el API en orden de dependencias. ¡Y secuestra tu terminal mostrándote los logs a colores combinados! |
| docker compose up -d | Lo mismo que arriba, pero lo hace silenciosamente devolviéndote el control de tu terminal. |
| docker compose up --build | Destruye la imagen de la API y la **vuelve a crear** si hiciste cambios de código en Java. |
| docker compose down | Apaga los 3 contenedores y destruye la red que los unía, pero los Discos Duros de Mongo y Postgres **se salvan**. |
| docker compose down -v | 🚨 **Peligro:** Apaga todo y destruye hasta los Discos Duros. Úsalo solo si quieres hacer "Factory Reset". |
| docker compose logs api | Solo quiero leer los logs de mi backend de Spring Boot, ignora lo de la base de datos. |
| docker compose logs -f | Escucho en tiempo real todo el ecosistema. |
| docker compose ps | Dime la salud de mis 3 contenedores. |
| docker compose restart api | Mi aplicación Java se atascó, pero no apagues mis bases de datos. ¡Solo apaga y prende Java! |

---

## ☁️ PASO 6: Subir la Imagen a la Nube (Docker Hub)

Hasta el Paso 5, nuestra imagen estaurante-api existía unicamente en el disco duro de nuestra computadora. Pero el verdadero poder de Docker brilla cuando utilizamos un **Registry** (como Docker Hub) para distribuir nuestra aplicación mundialmente.

### 1. Iniciar Sesión en la Nube
Primero, enlazamos nuestra terminal con nuestra cuenta de Docker Hub:
`ash
docker login
`

### 2. Nombrar y Etiquetar (Tag)
Docker necesita que el nombre de la imagen tenga tu nombre de usuario por delante para saber a qué cuenta pertenece. Etiquetamos nuestra imagen local para prepararla para el viaje:
`ash
docker build -t juandiegogta/restaurante-api:1.0 .
docker build -t juandiegogta/restaurante-api:latest .
`

### 3. El Lanzamiento (Push)
Subimos las imágenes a la nube pública de Docker:
`ash
docker push juandiegogta/restaurante-api:1.0
docker push juandiegogta/restaurante-api:latest
`

### 🧪 La Prueba Definitiva de Distribución
Para comprobar que la nube funciona, eliminamos completamente nuestra imagen local de la computadora:
`ash
docker rmi juandiegogta/restaurante-api:1.0
`

Y luego, le pedimos a Docker que ejecute el contenedor. ¡Al no encontrarlo localmente, lo buscará y descargará automáticamente de Docker Hub!
`ash
docker run -d -p 8080:8080 juandiegogta/restaurante-api:1.0

# Output:
# Unable to find image 'juandiegogta/restaurante-api:1.0' locally
# 1.0: Pulling from juandiegogta/restaurante-api
# Status: Downloaded newer image for juandiegogta/restaurante-api:1.0
`

¡Es mágico! Cualquier desarrollador del planeta puede correr tu aplicación ahora sin necesidad de tener el código fuente.

---

## ☸️ PASO 7: El Siguiente Nivel (Kubernetes - K8s)

Docker Compose es fantástico para tu máquina local o un servidor pequeño, pero ¿qué pasa si tu restaurante se vuelve un éxito mundial y recibes millones de peticiones por segundo? Docker Compose se quedaría corto. Aquí es donde entra **Kubernetes (K8s)**, el rey de la orquestación a gran escala.

Mientras Docker Compose maneja contenedores en una *sola* máquina, Kubernetes distribuye y orquesta contenedores a través de un **Clúster** (un ejército de máquinas conectadas), brindando superpoderes como:
- **Autoescalado:** Si hay mucho tráfico, clona la API 10 veces automáticamente.
- **Auto-recuperación:** Si un contenedor se "crashea", lo destruye y levanta uno nuevo en segundos.
- **Cero Downtime:** Actualiza tu código sin que los usuarios noten que el servidor se reinició.

### El Diccionario de Kubernetes
- 🏗️ **Pod:** La unidad atómica de K8s. Es una envoltura que contiene a tu contenedor (En el 99% de los casos, 1 Pod = 1 Contenedor).
- 📦 **Deployment:** El gerente. Define cuántos clones (réplicas) de tus Pods deben existir en todo momento.
- 🌐 **Service:** El enrutador. Como los Pods mueren y nacen con IPs distintas todo el tiempo, el Service les da una dirección IP estática para que el Frontend siempre sepa a dónde llamar.
- ⚙️ **ConfigMap / Secret:** El equivalente a nuestro .env, inyectado en el clúster de forma ultra-segura.

### Ejemplo Visual de un Deployment (YAML)
*(Este archivo es solo teórico para que comprendas la arquitectura, no necesitas aplicarlo para este proyecto local).*

`yaml
# k8s/deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: restaurante-api
spec:
  replicas: 2                        # 🚀 ¡Asegura 2 instancias corriendo siempre!
  selector:
    matchLabels:
      app: restaurante-api
  template:
    metadata:
      labels:
        app: restaurante-api
    spec:
      containers:
        - name: api
          image: juandiegogta/restaurante-api:latest # ¡Tu imagen descargada desde Docker Hub!
          ports:
            - containerPort: 8080
          env:
            - name: JWT_SECRET
              valueFrom:
                secretKeyRef:        # Extrae la clave de un K8s Secret encriptado
                  name: restaurante-secrets
                  key: jwt-secret
          readinessProbe:            # K8s hace ping aqui para saber si el contenedor ya terminó de encender
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 30
---
apiVersion: v1
kind: Service
metadata:
  name: restaurante-api-svc
spec:
  selector:
    app: restaurante-api
  ports:
    - port: 80
      targetPort: 8080
  type: LoadBalancer               # 🌍 Asigna una IP pública de internet al clúster
`

> 💡 **Nota de experimentación:** 
> Aunque para el curso desplegaremos en la nube de Azure usando CI/CD, si en algún momento quieres sentir el poder de Kubernetes en tu propia máquina, puedes activarlo yendo a *Docker Desktop -> Settings -> Kubernetes -> Enable Kubernetes*. 

---

## ✅ PASO 8: Verificación Final (Todo Funcionando)

El momento de la verdad. Al ejecutar el orquestador maestro, todo el restaurante debe cobrar vida por su cuenta.

### 1. Levantar el Stack Completo
`ash
docker compose up --build -d
`
Docker construirá silenciosamente el Dockerfile, levantará Postgres, levantará Mongo y finalmente levantará la API de Spring Boot.

### 2. Verificación de Salud
`ash
docker compose ps
`
Al correr este comando, debemos presenciar en la columna STATUS la palabra gloriosa: **healthy** (saludable) o unning.

### 3. Revisión de Bitácoras (Logs)
`ash
docker compose logs api
`
Comprobamos que Spring Boot imprima el hermoso texto Started RestauranteApplication in ... seconds y que no haya ningún rastro rojo de "Connection Refused" (que indicaría que la base de datos se cayó).

### 4. Pruebas Funcionales
- **Swagger UI:** Vamos al navegador y abrimos http://localhost:8080/swagger-ui/index.html.
- **Persistencia en Vivo:** Hacemos una petición POST creando un plato, reiniciamos el contenedor abruptamente (docker compose restart api) y comprobamos con un GET que el plato sigue existiendo porque guardó su información en los volúmenes de las bases de datos.

> 🏆 **Con esto, culminamos magistralmente la Semana 10. ¡Nuestra API es oficialmente "Cloud Native" y contenerizada!**
