# 03 — Arquitectura del Backend y Modelo MVC Desacoplado

## 1. Principio Rector: Desacoplamiento Estricto por Capas

El backend adopta una arquitectura multicapa (Layered Architecture) adaptada al diseño de APIs REST modernas sobre Spring Boot 3 y Java 21.

### Reglas Arquitectónicas Invariables:
1. **El Controller NUNCA ve una Entidad/Documento de MongoDB:** Solo interactúa con DTOs de entrada y salida y delega en el Mapper IN (`controller.mappers`) para hablar con el Dominio.
2. **El Service NUNCA ve un DTO:** La lógica de negocio opera únicamente sobre modelos de dominio puros (`core.models`).
3. **El Repositorio NUNCA contiene lógica de negocio:** Se encarga exclusivamente de la persistencia y consulta sobre documentos MongoDB (`persistence.entities`).
4. **Doble Capa de Mappers:**
   - **Mapper IN (`controller/mappers`):** DTO ↔ Dominio.
   - **Mapper OUT (`persistence/mappers`):** Dominio ↔ Document (MongoDB).

---

## 2. Mapa de Paquetes y Responsabilidades

```text
edu.dosw.parcial/
│
├── controller/                               # CAPA DE PRESENTACIÓN / CONTROLADOR
│   ├── dtos/
│   │   ├── request/                          # DTOs de entrada validados con Jakarta Validation
│   │   └── response/                         # DTOs de respuesta serializables a JSON
│   ├── mappers/                              # MAPPER IN: RequestDTO -> Dominio, Dominio -> ResponseDTO
│   ├── handlers/                             # GlobalExceptionHandler (@RestControllerAdvice)
│   ├── TurnoController.java                  # Endpoints de turnos
│   ├── AuthController.java                   # Endpoints de autenticación
│   ├── CatalogController.java                # Endpoints de consulta de catálogo
│   └── HealthController.java                 # Health check endpoint
│
├── core/                                     # CAPA DE DOMINIO Y LÓGICA DE NEGOCIO
│   ├── models/                               # Entidades puras de negocio (POJOs independientes de DB)
│   │   ├── Cliente.java, Servicio.java, Barbero.java, Turno.java, Usuario.java, PagoResultado.java
│   │   └── Excepciones de negocio (TurnoValidationException, ResourceNotFoundException)
│   ├── services/                             # Servicios de aplicación y casos de uso
│   │   ├── TurnoService.java, AuthService.java, CatalogService.java
│   │   ├── BarberRegistry.java, ServiceCatalog.java
│   │   └── adapters/                         # PATRÓN ADAPTER: Nequi, PSE, Stripe, Efectivo + Factory
│   ├── validators/                           # PATRÓN CHAIN OF RESPONSIBILITY: 5 Manejadores
│   │   ├── ValidationHandler.java, TimeAvailableHandler.java, BarberAvailableHandler.java,
│   │   │   ClientValidationHandler.java, CatalogValidationHandler.java, PaymentValidationHandler.java,
│   │   │   ValidationChainFactory.java
│   └── utils/                                # Clases de utilidad técnica (TimeSlotUtil)
│
├── persistence/                              # CAPA DE ACCESO A DATOS (MONGODB)
│   ├── entities/                             # Clases @Document de MongoDB (TurnoDocument, UserDocument, etc.)
│   ├── repositories/                         # Interfaces que extienden de MongoRepository
│   │   ├── TurnoRepository.java, UserRepository.java, ServiceDocumentRepository.java, ...
│   └── mappers/                              # MAPPER OUT: Dominio <-> Document MongoDB
│
├── config/                                   # INFRAESTRUCTURA Y CONFIGURACIÓN TRANSVERSAL
│   ├── SecurityConfig.java                   # Configuración perimetral de Spring Security
│   ├── JwtService.java                       # Firma y validación de tokens JJWT
│   ├── JwtAuthenticationFilter.java          # Filtro de cabecera Authorization Bearer
│   ├── CustomUserDetailsService.java         # Carga de credenciales y roles
│   ├── SwaggerConfig.java                    # Configuración OpenAPI 3.0 con soporte Bearer
│   └── DataInitializer.java                  # Inicialización y sembrado de datos en MongoDB
│
└── main/                                     # PUNTO DE ENTRADA DE LA APLICACIÓN
    └── ParcialApplication.java               # @SpringBootApplication con escaneo base
```

---

## 3. Diagrama de Secuencia: Flujo de una Solicitud `POST /api/v1/turnos`

```mermaid
sequenceDiagram
    autonumber
    actor Cliente as Cliente HTTP (Postman/Web)
    participant AuthFilter as JwtAuthenticationFilter
    participant Controller as TurnoController
    participant MapperIN as TurnoDtoMapper (IN)
    participant Service as TurnoService
    participant Chain as Chain of Responsibility (5 Handlers)
    participant Adapter as PaymentAdapterFactory / NequiAdapter
    participant MapperOUT as TurnoEntityMapper (OUT)
    participant Repo as TurnoRepository
    participant MongoDB as MongoDB Cluster

    Cliente->>AuthFilter: POST /api/v1/turnos (Bearer Token + JSON)
    AuthFilter->>AuthFilter: Valida firma y vigencia del JWT
    AuthFilter->>Controller: Petición autorizada con SecurityContext
    Controller->>Controller: Valida @Valid CreateTurnoRequest
    Controller->>MapperIN: toDomain(request)
    MapperIN-->>Controller: Retorna objeto puro Turno
    Controller->>Service: createTurno(turno)
    
    rect rgb(240, 248, 255)
        note right of Service: Ejecución Chain of Responsibility
        Service->>Chain: handle(turno)
        Chain->>Chain: 1. TimeAvailableHandler (aforo < 3)
        Chain->>Chain: 2. BarberAvailableHandler (asigna Miguel/Laura/Andres)
        Chain->>Chain: 3. ClientValidationHandler (nombre, 10 digitos, @gmail)
        Chain->>Chain: 4. CatalogValidationHandler (código en catálogo)
        Chain->>Adapter: 5. PaymentValidationHandler -> NequiAdapter.processPayment()
        Adapter-->>Chain: Retorna PagoResultado (NQ-XXXX, APROBADO)
        Chain-->>Service: Retorna ValidationResult.success()
    end

    Service->>MapperOUT: toDocument(turno)
    MapperOUT-->>Service: Retorna TurnoDocument
    Service->>Repo: save(turnoDocument)
    Repo->>MongoDB: Insert Document en colección 'turnos'
    MongoDB-->>Repo: Document persistido con ID
    Repo-->>Service: Retorna TurnoDocument
    Service->>MapperOUT: toDomain(savedDocument)
    MapperOUT-->>Service: Retorna Turno
    Service-->>Controller: Retorna Turno
    Controller->>MapperIN: toResponse(turno)
    MapperIN-->>Controller: Retorna TurnoResponse DTO
    Controller-->>Cliente: HTTP 201 Created (JSON TurnoResponse)
```
