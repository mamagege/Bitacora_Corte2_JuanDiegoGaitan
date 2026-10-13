Para implementar en este formato:
DOSW · Escuela Colombiana de Ingeniería Julio Garavito
Parcial Práctico — Segundo Tercio
Parte 1.1 · Prerrequisitos
Antes de entrar al laboratorio deben llevar listo el proyecto base: estructura MVC, README organizado, dependencias, conexión a MongoDB, Dockerfile, pipeline y el servicio desplegado en Azure, aunque todavía esté vacío.

Crear el proyecto Maven con
groupId: edu.dosw.parcial
y
artifactId: DOSW-ParcialT2
.
Verificar que
.env
,
*.p12
y
target/
estén en el
.gitignore
. Ninguna credencial se sube al repositorio.
2. Scaffolding MVC completo
Estructura como la vimos en clase
Creen todos los paquetes desde el inicio, incluso los que queden vacíos. Git no versiona carpetas vacías: agreguen un archivo .gitkeep en cada una para que aparezcan en el repositorio. La carpeta docs/ se crea manualmente.

DOSW_ParcialT2_PrimerNombrePrimerApellido/
│
├── docs/
│   ├── uml/                      # diagramas fuente (draw.io / Lucidchart / Miro)
│   └── images/                   # exportes PNG/SVG y evidencias del README
│
├── src/
│   ├── main/
│   │   ├── java/edu/dosw/parcial/
│   │   │   ├── controller/
│   │   │   │   ├── dtos/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   ├── mappers/      # Mapper IN: DTO ↔ Dominio
│   │   │   │   └── handlers/     # GlobalExceptionHandler
│   │   │   │
│   │   │   ├── core/
│   │   │   │   ├── services/
│   │   │   │   ├── models/       # objetos de dominio
│   │   │   │   ├── validators/
│   │   │   │   └── utils/
│   │   │   │
│   │   │   ├── persistence/
│   │   │   │   ├── entities/     # clases @Document (MongoDB)
│   │   │   │   ├── repositories/ # MongoRepository
│   │   │   │   └── mappers/      # Mapper OUT: Dominio ↔ Document
│   │   │   │
│   │   │   ├── config/           # SecurityConfig, SwaggerConfig
│   │   │   └── main/             # clase con @SpringBootApplication
│   │   │
│   │   └── resources/
│   │       └── application.yaml  # o application.properties (usar solo uno)
│   │
│   └── test/
│       ├── java/edu/dosw/parcial/
│       │   ├── controller/
│       │   ├── core/
│       │   └── persistence/
│       └── resources/
│
├── Dockerfile
├── .dockerignore
├── .github/workflows/            # pipeline
├── pom.xml
├── .gitignore
└── README.md
Dos mappers, uno por tipo de objeto.
En controller/mappers va el Mapper IN (RequestDTO → Dominio, Dominio → ResponseDTO). En persistence/mappers va el Mapper OUT (Dominio ↔ Document). El Controller nunca ve un Document y el Service nunca ve un DTO.
Carpeta entities/ con MongoDB.
Mantenemos el nombre entities de la estructura de clase, pero las clases dentro se anotan con @Document en lugar de @Entity.
La clase principal vive en el paquete main/, que está dentro de edu.dosw.parcial.
El escaneo de componentes de Spring parte del paquete de la clase anotada con @SpringBootApplication. Como main es un subpaquete, Spring no encontraría los demás paquetes. Para que funcione, indiquen el paquete base explícitamente (ver sección 4).
3. Dependencias y plugins en el pom.xml
Lo que debe estar agregado
El PDF original pide JPA como persistencia relacional. En este parcial la persistencia es MongoDB, así que reemplazan el starter de JPA por el de Spring Data MongoDB.

Requisito	Dependencia / plugin
Spring Boot Web	spring-boot-starter-web
MongoDB	spring-boot-starter-data-mongodb
Validación de DTOs	spring-boot-starter-validation
JWT + Spring Security	spring-boot-starter-security + jjwt-api, jjwt-impl, jjwt-jackson
Swagger	springdoc-openapi-starter-webmvc-ui
Lombok	lombok (optional) + annotation processor
MapStruct	mapstruct + mapstruct-processor + lombok-mapstruct-binding
Logs SLF4J	Ya viene con Spring Boot. Se usa con @Slf4j de Lombok.
JUnit y Mockito	spring-boot-starter-test (incluye ambos)
Jacoco	jacoco-maven-plugin (goals prepare-agent y report)
SonarQube	sonar-maven-plugin + propiedades sonar.*
Fragmento de referencia
<properties>
    <java.version>21</java.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <jjwt.version>0.12.3</jjwt.version>
</properties>

<dependencies>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-mongodb</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-validation</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security</artifactId></dependency>

    <dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-api</artifactId><version>${jjwt.version}</version></dependency>
    <dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-impl</artifactId><version>${jjwt.version}</version><scope>runtime</scope></dependency>
    <dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-jackson</artifactId><version>${jjwt.version}</version><scope>runtime</scope></dependency>

    <dependency><groupId>org.springdoc</groupId><artifactId>springdoc-openapi-starter-webmvc-ui</artifactId><version>2.5.0</version></dependency>

    <dependency><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId><optional>true</optional></dependency>
    <dependency><groupId>org.mapstruct</groupId><artifactId>mapstruct</artifactId><version>${mapstruct.version}</version></dependency>

    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><scope>test</scope></dependency>
    <dependency><groupId>org.springframework.security</groupId><artifactId>spring-security-test</artifactId><scope>test</scope></dependency>
</dependencies>

<build>
  <plugins>
    <plugin><groupId>org.springframework.boot</groupId><artifactId>spring-boot-maven-plugin</artifactId></plugin>

    <plugin>
      <groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId>
      <configuration>
        <annotationProcessorPaths>
          <!-- ORDEN IMPORTANTE: lombok → lombok-mapstruct-binding → mapstruct-processor -->
          <path><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId><version>${lombok.version}</version></path>
          <path><groupId>org.projectlombok</groupId><artifactId>lombok-mapstruct-binding</artifactId><version>0.2.0</version></path>
          <path><groupId>org.mapstruct</groupId><artifactId>mapstruct-processor</artifactId><version>${mapstruct.version}</version></path>
        </annotationProcessorPaths>
      </configuration>
    </plugin>

    <plugin>
      <groupId>org.jacoco</groupId><artifactId>jacoco-maven-plugin</artifactId><version>0.8.12</version>
      <executions>
        <execution><goals><goal>prepare-agent</goal></goals></execution>
        <execution><id>report</id><phase>test</phase><goals><goal>report</goal></goals></execution>
      </executions>
    </plugin>

    <plugin><groupId>org.sonarsource.scanner.maven</groupId><artifactId>sonar-maven-plugin</artifactId><version>3.10.0.2594</version></plugin>
  </plugins>
</build>
Versiones de referencia, verifíquenlas.
Las versiones de springdoc, jjwt, MapStruct, Jacoco y sonar-maven-plugin son las que usamos en las guías. Confirmen en Maven Central cuál es la estable más reciente compatible con su versión de Spring Boot. La versión de Lombok la gestiona el BOM de Spring Boot: definan ${lombok.version} o usen la versión que Spring Boot ya trae.
4. Configuración: MongoDB, seguridad y clase principal
El servicio vacío debe arrancar
application.yaml
spring:
  application:
    name: DOSW-ParcialT2
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/dosw_parcial}   # local por defecto; en Azure viene de variable de entorno

server:
  port: ${PORT:8080}

jwt:
  secret: ${JWT_SECRET}                # Base64, mínimo 44 caracteres. Nunca hardcodeado
  expiration-ms: 3600000

springdoc:
  swagger-ui:
    path: /swagger-ui.html
Clase principal (paquete main)
package edu.dosw.parcial.main;

@SpringBootApplication(scanBasePackages = "edu.dosw.parcial")
@EnableMongoRepositories(basePackages = "edu.dosw.parcial.persistence.repositories")
public class ParcialApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParcialApplication.class, args);
    }
}
Endpoint de salud mínimo (para verificar el despliegue vacío)
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    @GetMapping
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "DOSW-ParcialT2");
    }
}
SecurityConfig base (en config/)
Con Spring Security en el classpath, todo queda bloqueado por defecto.
Si no configuran nada, el servicio desplegado responde 401 incluso en Swagger y en el health. El siguiente fragmento deja públicos solo esos endpoints. La seguridad real (login, JWT, roles) se implementa en el laboratorio.
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/health", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated())
            .build();
    }
}
Credenciales de base de datos.
Configuren la conexión a MongoDB en el archivo de configuración leyendo la URI desde variable de entorno (MONGODB_URI). Para desarrollo local pueden levantar Mongo con docker run -d -p 27017:27017 --name mongo-parcial mongo:7. Nunca escriban la URI con usuario y contraseña en el repositorio.
5. Dockerfile del servicio
Imagen multietapa
# Etapa 1: compilar
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# Etapa 2: imagen final liviana
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
# .dockerignore
.git
.github
target/
docs/
*.md
.env
*.p12
docker build -t dosw-parcial:local .
termina sin errores.
docker run -p 8080:8080 -e JWT_SECRET=<su_secret_base64> dosw-parcial:local
arranca y
http://localhost:8080/api/v1/health
responde 200.
6. Pipeline base con GitHub Actions
Build · pruebas · imagen · despliegue
El pipeline base compila, ejecuta las pruebas, construye la imagen, la publica en Docker Hub y la despliega en Azure. Va en .github/workflows/ci-cd.yml. Para el detalle de cada paso, apóyense en la Guía Turtwig S10 de CI/CD.

name: CI/CD — DOSW Parcial T2

on:
  push:
    branches: [ main, develop ]

env:
  IMAGE_NAME: ${{ secrets.DOCKERHUB_USERNAME }}/dosw-parcial-t2

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '21', distribution: 'temurin', cache: maven }
      - name: Build y pruebas
        run: mvn verify --no-transfer-progress
        env:
          JWT_SECRET: ${{ secrets.JWT_SECRET }}

  docker:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}
      - uses: docker/build-push-action@v5
        with:
          push: true
          tags: ${{ env.IMAGE_NAME }}:latest

  deploy:
    needs: docker
    runs-on: ubuntu-latest
    steps:
      - uses: azure/login@v2
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}
      - uses: azure/webapps-deploy@v3
        with:
          app-name: ${{ secrets.AZURE_WEBAPP_NAME }}
          images: ${{ env.IMAGE_NAME }}:latest
Secret en GitHub	Contenido
DOCKERHUB_USERNAME	Su usuario de Docker Hub
DOCKERHUB_TOKEN	Access token de Docker Hub (no la contraseña)
AZURE_CREDENTIALS	JSON del Service Principal con acceso al grupo de recursos
AZURE_WEBAPP_NAME	Nombre del App Service creado en Azure
JWT_SECRET	Secret JWT Base64 (mínimo 44 caracteres)
Si el repositorio tiene pruebas con contexto de Spring (@SpringBootTest)
, necesitan MongoDB disponible en el pipeline o deben excluirse/mockearse. Para el servicio vacío, con las pruebas unitarias (JUnit + Mockito) alcanza.
7. Despliegue en Azure
El servicio en línea, aunque esté vacío
Crear un
Azure App Service
(Linux, publicación por contenedor) apuntando a la imagen de Docker Hub
usuario/dosw-parcial-t2:latest
.
Configurar las variables de entorno en el App Service (Configuration → Application settings):
MONGODB_URI
,
JWT_SECRET
y
WEBSITES_PORT=8080
para que Azure sepa a qué puerto enrutar.
Crear el Service Principal y guardar su JSON como secret
AZURE_CREDENTIALS
(comando en la guía S10 de CI/CD).
Contar con una base MongoDB accesible desde Azure (por ejemplo, Azure Cosmos DB for MongoDB o un clúster gestionado) y usar su cadena de conexión como
MONGODB_URI
.
Hacer push a
develop
y verificar que el pipeline queda en verde.
Abrir
https://<nombre>.azurewebsites.net/api/v1/health
y comprobar que responde 200.
Abrir
https://<nombre>.azurewebsites.net/swagger-ui/index.html
y comprobar que carga Swagger.
Costos y cuotas.
Revisen que su suscripción (por ejemplo, Azure for Students) cubra App Service y la base de datos elegida. Si algo falla por plan o región, comuníquenlo antes del laboratorio, no durante.

10. Entregables y verificación final
Qué debe estar listo antes de entrar al laboratorio
✅ Checklist de entrega
Repositorio con el nombre correcto, README y .gitignore, rama develop y profesor agregado
Estructura MVC completa con carpeta docs/ (uml/ e images/)
pom.xml con todas las dependencias y plugins; mvn clean verify pasa
Conexión a MongoDB configurada por variable de entorno (sin credenciales en el repositorio)
Dockerfile y .dockerignore; la imagen construye y corre
Pipeline en .github/workflows en verde
Servicio desplegado en Azure: health y Swagger responden desde la URL pública
README con las 11 secciones, índice y capturas como evidencia
Evidencia de cuentas en herramienta de modelado y Figma

