# SECCION S09: PERSISTENCIA Y AUDITORIA DE CALIDAD

## ðŸ—ºï¸ Antes de empezar: Lo que cambia y lo que se mantiene

La evolucion a la Seccion 09 implica reemplazar las estructuras en memoria (ConcurrentHashMap) por una Base de Datos real (PostgreSQL). Sin embargo, gracias a nuestra arquitectura en capas, **el 90% del proyecto queda intacto**.

### âœ… Lo que se MANTIENE INTACTO
- **Objetos de dominio:** La clase Plato sigue sin anotaciones de Spring ni JPA.
- **DTOs Request / Response:** La capa web y sus validaciones (@NotBlank) siguen funcionando igual.
- **Controller + Rutas:** Al no tener logica, ignora si los datos vienen de memoria o de BD.
- **Validators de Negocio:** La logica sigue siendo la misma.
- **GlobalExceptionHandler y Swagger:** Totalmente blindados del cambio.
- **Streams en el Service:** Seguimos operando colecciones de la misma manera funcional.

### ðŸ”„ Lo que CAMBIA
- **Service:** Ahora inyecta y utiliza un **Repository** en lugar de un HashMap.
- **Mapper:** Gana una nueva frontera de traduccion: 	oEntity() y romEntity().
- **Nuevo Actor JPA:** Aparecen las @Entity para modelar las tablas.

> ðŸŒ¿ **El principio guia:** El Dominio no sabe NADA de la base de datos. La entidad JPA es exclusiva de infraestructura. Esto blinda al negocio ante cualquier migracion (Ej. pasar de PostgreSQL a MongoDB manana).

---

## PASO 01: Persistencia Relacional â€” Entidades y Repositorios

En esta etapa consolidamos nuestra conexion a PostgreSQL mediante **Spring Data JPA**.

### El Anti-patron Mortal âŒ
El error mas grave de arquitectura backend es **anotar tus objetos de dominio con @Entity**. 
Si haces eso, la Base de Datos dicta como debe comportarse tu negocio, te impide cambiar el esquema de las tablas sin romper el software y esparce anotaciones JPA por toda tu capa web.

### La Solucion Arquitectonica âœ…
Separamos fisicamente:
- model/domain/Plato.java -> Solo logica.
- persistence/entity/PlatoEntity.java -> Solo anotaciones de Base de Datos.

### 1. La Entidad JPA (PlatoEntity.java)
La clase representa 1 a 1 a la tabla platos en PostgreSQL:

``java
@Entity
@Table(name = "platos")
public class PlatoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false, length = 60)
    private String categoria;

    private String descripcion;

    @Column(nullable = false)
    private Boolean disponible;

    @CreationTimestamp
    private LocalDateTime creadoEn;
}
``

> ðŸ’¡ **Nota sobre el tipo de Precio:** En nuestro proyecto academico usamos Double. Para entornos empresariales criticos o fintech, siempre se debe usar BigDecimal junto a @Column(precision=10, scale=2) para evitar perdidas de punto flotante en las decimas (ej. 0.1 + 0.2 != 0.3).

### 2. El Repository Magico (PlatoRepository.java)
En Spring Data JPA, no escribimos SQL. Simplemente declaramos los metodos siguiendo una convencion de nomenclatura ("Query Methods") y Spring compila el SQL por nosotros en tiempo de ejecucion:

```java
@Repository
public interface PlatoRepository extends JpaRepository<PlatoEntity, Long> {

    // Extrae los platos listos para el menu del cliente
    List<PlatoEntity> findByDisponibleTrue();
    
    // Filtrado agnostico de mayusculas/minusculas
    List<PlatoEntity> findByCategoriaIgnoreCase(String categoria);
    
    // Optimizacion para los validadores de negocio (devuelve booleano, muy rapido)
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
```
*La interfaz hereda de `JpaRepository` con lo cual ganamos gratis metodos base como `save()`, `findAll()`, `findById()`, `count()`, y `delete()` sin haber programado ni una sola linea.*

---

## ðŸªª PASO 01B: Tipos de ID â€” Â¿Long, UUID v4 o UUID v7?

El tipo de ID que elijas para tu entidad **no es un detalle menor**. Afecta directamente el rendimiento de los Ã­ndices, la seguridad de tus endpoints y la capacidad de tu sistema de trabajar en entornos distribuidos.

### 1. Long (`IDENTITY`)
El clÃ¡sico ID numÃ©rico autoincremental asignado por la base de datos.
```java
@Id 
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
- **CuÃ¡ndo usarlo:** Entidades internas de soporte, catÃ¡logos (CategorÃ­as, Roles, Estados) y **Platos**. Datos que no necesitan ocultar su volumen y priorizan la velocidad de los `JOINs`.
- **CuÃ¡ndo evitarlo:** Entidades pÃºblicas (Pedidos, Reservas, Usuarios). Revelar `/api/pedidos/42` expone a la competencia cuÃ¡ntos pedidos procesas y facilita el raspado de datos (Ataque de EnumeraciÃ³n).

### 2. UUID v4 (Aleatorio)
Identificadores Ãºnicos universales generados de forma aleatoria.
```java
@Id 
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```
- **CuÃ¡ndo usarlo:** URLs pÃºblicas (Ej. ConfirmaciÃ³n de una Reserva). Imposibles de adivinar y ocultan el volumen de registros de tu BD.
- **CuÃ¡ndo evitarlo:** En tablas con millones de `INSERTS` por segundo. Al ser completamente aleatorios, destruyen/fragmentan el Ã­ndice `B-tree` de la base de datos, ralentizando severamente la escritura. Ocupan mÃ¡s bytes en cada llave forÃ¡nea.

### 3. UUID v7 (Ordenado por Tiempo)
*Lo mejor de ambos mundos.*
```java
// Spring Boot 3.2+ / Hibernate 6.4+
@Id 
@UuidGenerator(style = UuidGenerator.Style.TIME)
private UUID id;
```
- **Por quÃ© es el futuro:** Conserva la seguridad e impredecibilidad aparente del UUID v4, pero los primeros 48 bits contienen un *timestamp*. Esto hace que los UUIDs se inserten de manera secuencial en la BD, manteniendo el Ã­ndice B-tree sano y logrando un rendimiento de escritura casi idÃ©ntico al del `Long`. 
- **En nuestro Restaurante:** Ideal para registros de alta velocidad y auditorÃ­a, como `Pedidos` o `Logs de transacciones`.

### 4. String (`ObjectId` - MongoDB)
Si trabajÃ¡ramos con Mongo, utilizarÃ­amos su propio `ObjectId` de 12 bytes. Es equivalente a un UUID v7 (incluye un timestamp, un valor aleatorio y un contador) y no fragmenta los Ã­ndices.

> ðŸ” **DecisiÃ³n ArquitectÃ³nica para el Restaurante:**
> Nuestro dominio `PlatoEntity` usa **`Long`** porque es un catÃ¡logo interno del negocio. Un cliente no accede a sus pedidos pidiendo `/platos/1` sino a travÃ©s del `/menu` global, por lo que no hay riesgo de enumeraciÃ³n. Para las `Reservas` y `Pedidos`, utilizaremos **UUID v4 o v7** para garantizar que un cliente no pueda adivinar el pedido de la mesa de al lado.

---

## PASO 02: El Service y el Nuevo Validator (Streams + BD)

La magia de esta migracion es que nuestro PlatoServiceImpl **no cambio ni una sola linea** en su contrato web (sigue recibiendo y devolviendo Plato), pero su motor interno paso de ser un diccionario en memoria a ser un Repositorio de Base de Datos.

### 1. El Nuevo Service (PlatoServiceImpl.java)
Ahora inyectamos tres dependencias maestras:
- PlatoRepository: Para interactuar con PostgreSQL.
- PlatoEntityMapper: Para traducir de Entidad a Dominio.
- IPlatoValidator: Para aislar las reglas de negocio.

`java
    @Override
    public List<Plato> obtenerDisponibles() {
        return platoRepository.findByDisponibleTrue().stream()
                .map(entityMapper::toDomain)
                .toList();
    }
`
*Aca vemos como el Service llama a la BD y de inmediato mapea el resultado al Dominio. Las operaciones complejas siguen fluyendo a traves de Java Streams si es necesario.*

### 2. El Manejo de Transacciones (@Transactional)
En los metodos de escritura (crear, ctualizar, cambiarDisponibilidad), el Service ahora utiliza @Transactional.
Â¿Por que? Porque en el metodo ctualizar, primero hacemos un indById() y luego modificamos y hacemos un save(). Si ocurre un error critico a la mitad, la BD hara *rollback* para evitar inconsistencias.

### 3. La Evolucion del Validator (PlatoValidator.java)
Anteriormente, el Validador recibia por parametro la inmensa lista de ConcurrentHashMap.values() y hacia un .stream().anyMatch(...). Esto en Base de Datos seria un **suicidio de rendimiento** (Â¡Imagina traer 100,000 platos a RAM solo para buscar un duplicado!).

Con JPA, el Validator evoluciona y ahora se encarga de hablarle directamente al Repository:

`java
@Component
@RequiredArgsConstructor
public class PlatoValidator implements IPlatoValidator {

    private final PlatoRepository platoRepository;

    @Override
    public void validarNombreUnico(String nombre) {
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe un plato con el nombre '" + nombre + "'");
        }
    }

    @Override
    public void validarNombreUnicoExcluyendo(String nombre, Long id) {
        if (platoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoException("Ya existe otro plato con el nombre '" + nombre + "'");
        }
    }
}
`

> ðŸ” **Q&A Arquitectonico:**
> **Â¿Quien asigna el ID ahora?** La Base de Datos. Observa que ya no usamos AtomicLong ni generamos el ID manual en el crear. Todo ocurre por el @GeneratedValue en PlatoEntity.
> **Â¿Por que Plato y PlatoEntity son distintas?** Porque tienen responsabilidades que cambian por razones distintas. Plato gobierna reglas algoritmicas del restaurante. PlatoEntity es un esclavo de como las tablas se relacionan y optimizan en Postgres.

---

## ðŸŽ­ PASO 03: El Mapper evoluciona a 3 capas

Con la introduccion de la Base de Datos, nuestro sistema de mapeo evoluciona. Ya no basta con traducir entre JSON (DTO) y Dominio. Ahora tambien necesitamos traducir entre Dominio y la Base de Datos (Entity).

Esto nos lleva al poderoso **Patron de las 3 Capas de Traduccion**.

### El Flujo de 3 Capas
1. **Peticion JSON** â†’ PlatoMapper.toDomain() â†’ **Dominio** (En el Controller)
2. **Dominio** â†’ PlatoEntityMapper.toEntity() â†’ **Entidad JPA** (En el Service, para guardar en BD)
3. **Entidad JPA** â†’ PlatoEntityMapper.toDomain() â†’ **Dominio** (En el Service, al leer de BD)
4. **Dominio** â†’ PlatoMapper.toResponse() â†’ **Respuesta JSON** (En el Controller)

> ðŸš¨ **La Regla Fronteriza:**
> El Controller **jamas** debe conocer la existencia de un PlatoEntity.
> El Service **jamas** debe conocer la existencia de un RequestDTO o ResponseDTO.
> El lenguaje comun entre ellos es siempre el Objeto de Dominio Puro.

### Interfaces MapStruct Especializadas
Por lo tanto, la arquitectura define dos interfaces distintas, aplicando el Principio de Segregacion de Interfaces (ISP):

#### 1. PlatoMapper (Inyectado en el Controller)
Se encarga exclusivamente de la capa de Presentacion:
`java
@Mapper(componentModel = "spring")
public interface PlatoMapper {
    @Mapping(target = "id",         ignore = true)
    @Mapping(target = "disponible", constant = "true")
    Plato            toDomain(PlatoRequestDTO dto);
    PlatoResponseDTO toResponse(Plato plato);
    List<PlatoResponseDTO> toResponseList(List<Plato> platos);
}
`

#### 2. PlatoEntityMapper (Inyectado en el Service)
Se encarga exclusivamente de la capa de Persistencia:
`java
@Mapper(componentModel = "spring")
public interface PlatoEntityMapper {

    // El ID y los demas campos de negocio se conservan, 
    // pero omitimos la fecha automatica generada por Hibernate
    @Mapping(target = "creadoEn", ignore = true)
    PlatoEntity toEntity(Plato plato);

    Plato toDomain(PlatoEntity entidad);
}
`

> âš ï¸ **Punto Ciego: Creacion vs Actualizacion**
> Cuando *Creas* un recurso, el id es 
ull, y la Base de Datos genera uno nuevo (vÃ­a @GeneratedValue).
> Cuando *Actualizas* un recurso, debes asegurarte de rescatar la entidad original usando indById(), y luego reescribir solo los valores editables sobre ella. De este modo, Hibernate detecta que la entidad existe en su contexto transaccional y ejecuta limpiamente un UPDATE en lugar de tratar de forzar un escabroso nuevo INSERT.

---

## ðŸƒ PASO 03: Persistencia No Relacional â€” MongoDB

En arquitecturas avanzadas empresariales, no toda la data del negocio encaja en las rigidas restricciones de una base de datos relacional (ACID). Para eventos de alto volumen, auditorias y catalogos con estructura variable, **MongoDB** es el aliado perfecto.

Nuestra arquitectura multi-bd soporta esta migracion mediante **Spring Data MongoDB**, operando bajo el mismo patron que JPA pero utilizando un Document en lugar de un Entity.

### 1. Â¿Cuando usar MongoDB vs Postgres en nuestro Restaurante?

> âœ… **Buena eleccion para MongoDB (Estructuras Flexibles / Alta Velocidad)**
> - Historial de cambios de estado de pedidos (Log de eventos).
> - Logs de auditoria (Que hizo el administrador y cuando).
> - Documentos de configuracion del restaurante.

> âš ï¸ **Mejor en PostgreSQL (ACID CrÃ­tico / Integridad Relacional Estricta)**
> - Facturas, Cuentas y Pagos.
> - Mesas y reservas.
> - Usuarios, Roles y Seguridad.

### 2. El Documento de Auditoria (EventoRestauranteDocument.java)
Observemos la anatomia del Documento encargado de registrar cada cambio en el restaurante:

`java
@Document(collection = "eventos_restaurante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoRestauranteDocument {

    @Id
    private String id;  // Mongo usa String nativamente (ObjectId en BSON)

    private String        tipo;         // "PEDIDO_CREADO", "PLATO_AGOTADO"
    private String        entidadTipo;  // "Pedido", "Plato"
    private Long          entidadId;
    private String        descripcion;
    private String        usuario;
    private LocalDateTime timestamp;

    // MAGIA NO RELACIONAL: Campos opcionales variables por cada evento.
    // Esto es un suicidio en SQL (EAV Antipattern), pero es nativo en Mongo.
    private Map<String, Object> metadatos;
}
`

### 3. El MongoRepository
De la misma forma magica que JpaRepository, podemos consultar documentos JSON mediante Query Methods en MongoRepository:

`java
@Repository
public interface EventoRestauranteRepository extends MongoRepository<EventoRestauranteDocument, String> {

    // Extrae todo el historial de vida de un Plato especifico
    List<EventoRestauranteDocument> findByEntidadTipoAndEntidadId(String entidadTipo, Long entidadId);

    // Consulta rangos de tiempo de eventos criticos
    List<EventoRestauranteDocument> findByTipoAndTimestampBetween(String tipo, LocalDateTime desde, LocalDateTime hasta);
}
`

> ðŸ” **Â¿Embebido vs Referenciado? (Decisiones No-Relacionales):**
> Si los datos de un subdocumento *solo tienen sentido* viviendo dentro de su documento padre (Ej. los "ingredientes opcionales" de un pedido), **embeberlos** como listas es correcto y muy veloz. Si el subdocumento se comparte o cambia por su cuenta (Ej. la configuracion del usuario), se debe referenciar con un ID, igual que una llave forÃ¡nea.

---

## ðŸŽ­ PASO 04: DocumentMapper â€” El patron no relacional

Asi como protegimos la logica del sistema relacionando el PlatoEntity a traves de un Mapper, con MongoDB el patron **es exactamente el mismo**. El Dominio no tiene idea de si sus datos se estan guardando en Postgres, en Mongo o en un archivo de texto.

### 1. El Objeto de Dominio (EventoRestaurante.java)
Reside en model/domain/ como un simple objeto Java puro (POJO). Tiene los mismos campos logicos, pero ignora completamente el id autogenerado de Mongo y cualquier anotacion ajena a las reglas de negocio.

### 2. El Mapper Exclusivo (EventoMapper.java)
Instruimos a MapStruct para que maneje la traduccion desde la capa de Negocio hacia la Coleccion de Mongo:

`java
@Mapper(componentModel = "spring")
public interface EventoMapper {

    // Dominio â†’ Documento (Al guardar en MongoDB)
    // El id es generado dinamicamente por BSON al hacer save()
    @Mapping(target = "id", ignore = true)
    EventoRestauranteDocument toDocument(EventoRestaurante evento);

    // Documento â†’ Dominio (Al extraer la auditoria de MongoDB)
    EventoRestaurante toDomain(EventoRestauranteDocument doc);
}
`

> ðŸ” **El Flujo Arquitectonico Unificado**
> Este nivel de arquitectura desacoplada significa que el viaje de los datos sigue un tubo esterilizado y estandar, sin importar la tecnologia final:
> 
> **Escritura:**
> JSON Request -> Mapper(toDomain) -> Service -> Mapper(toEntity / toDocument) -> Repository(SQL / Mongo)
> 
> **Lectura:**
> Repository(SQL / Mongo) -> Mapper(toDomain) -> Service -> Mapper(toResponse) -> JSON Response

Este patron de diseno, donde el Dominio actua como lengua franca y los Mappers como traductores fronterizos, es la clave definitiva para construir software capaz de soportar a cientos de miles de usuarios sin que el codigo se vuelva un espagueti inmanejable.

---

## ðŸŒŠ PASO 05: Los Streams sobreviven la migracion

Lo mas bello de una arquitectura bien pensada es su resiliencia al cambio. A pesar de haber borrado nuestro ConcurrentHashMap por completo para introducir motores de base de datos relacionales y no relacionales, **las manipulaciones de Streams en el Service apenas cambiaron**.

### La pequeÃ±a gran diferencia (El Mapper Intermedio)

**En la Memoria (S08):**
`java
public List<Plato> obtenerDisponibles() {
    return platos.values().stream()
            .filter(Plato::estaDisponible)
            .toList();
}
`

**Con Persistencia (S09):**
`java
public List<Plato> obtenerDisponibles() {
    return platoRepository.findByDisponibleTrue().stream()
            .map(entityMapper::toDomain)   // â† unica diferencia visual
            .toList();
}
`

El .map(entityMapper::toDomain) se encarga de convertir cada PlatoEntity proveniente de la base de datos a un Plato de dominio antes de que el Stream siga su curso. AsÃ­, el resto de operaciones y logicas matematicas sobre la colecciÃ³n siguen trabajando en su idioma nativo sin enterarse del cambio de arquitectura.

### ðŸ’¡ Leccion CrÃ­tica: Filtra en la BD, no en el Stream
Aunque podrias hacer un platoRepository.findAll().stream().filter(...), **es el peor anti-patrÃ³n de persistencia posible**.
Â¿Por quÃ©? Porque estas obligando a la Base de Datos a extraer, serializar y enviarte un millon de registros a la memoria RAM de tu servidor de Java, solo para que Java descarte el 90% de ellos en un milisegundo.

Siempre que sea posible:
1. Delega el filtrado pesado a las *Query Methods* de Spring Data (indByCategoriaIgnoreCase). La Base de Datos usa **Ã­ndices nativos** ultrarrÃ¡pidos para esto.
2. Utiliza los Streams de Java *exclusivamente* para transformaciones, sumatorias, agrupaciones y mapeos (.map, .reduce, .collect).

---

## â¸ Repaso de Cierre (Q&A del Sistema de Persistencia)

**1. Â¿Por que PlatoEntity y Plato son clases distintas?**
Porque sufren cambios por razones abismalmente distintas. Plato es gobernado por las reglas del gerente comercial del restaurante. PlatoEntity es gobernado por el administrador de base de datos, quien puede querer cambiar el nombre de una columna, agregar constraints @Column(length=50) o cambiar la estrategia de generaciÃ³n de ID. Mezclarlas en un mismo archivo es romper el Principio de Responsabilidad Unica (SRP).

**2. Â¿QuiÃ©n asigna el ID ahora, Java o la Base de Datos?**
La Base de Datos, gracias a @GeneratedValue(strategy = GenerationType.IDENTITY) para SQL o el generador de ObjectId de MongoDB. Por ello nuestro Dominio ya no necesita llevar la cuenta de ningun AtomicLong. El ID le es devuelto al Service despues de que ejecuta .save().

**3. Â¿Por que el ServiceImpl inyecta a PlatoEntityMapper si el Controller ya inyecta a PlatoMapper?**
Porque cada Mapper es la aduana de una frontera distinta. 
- PlatoMapper intercepta el borde **Exterior** (Web/JSON) y jamas entra al Service. 
- PlatoEntityMapper intercepta el borde **Interior** (Base de Datos) y jamas sale del Service. 
Si inyectaras ambos en el Controller o en el Service, cruzarias las fronteras y contaminarias las responsabilidades del sistema.

---

## ðŸ” PASO 06: Seguridad â€” Autenticacion con JWT (Stateless)

El Restaurante no guarda estado ni "sesiones" HTTP tradicionales. Cada vez que un mesero o administrador hace una peticion, debe identificarse en la misma peticion a traves de un **Token JWT firmado**. 

El JWT (JSON Web Token) le dice al servidor "Soy yo, tengo estos permisos, y mi token aun no expira". Todo sin tener que ir a buscar a la base de datos en cada peticion.

### 1. El Flujo de Identificacion
1. El usuario llama a POST /auth/login con sus credenciales.
2. El AuthController valida contra la BD y genera un Token firmado gracias a JwtUtil.
3. El cliente recibe el token (eyJ...) y lo guarda.
4. En cada peticion posterior (Ej. Crear un Plato), el cliente incluye el header: Authorization: Bearer eyJ....
5. El interceptor de Spring (JwtAuthFilter) verifica matemÃ¡ticamente la firma del token antes de que llegue al Controller. Si es invalido, se rebota con un **401 Unauthorized**.

### 2. Generacion y Validacion (JwtUtil)
El motor de los tokens, utilizando una llave secreta de 32 bytes (Base64), sella la identidad del usuario y sus roles:
`java
public String generarToken(String email, List<String> roles) {
    return Jwts.builder()
            .subject(email)
            .claim("roles", roles) // Los roles viajan publicamente en el token
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getKey())
            .compact();
}
`

### 3. El Filtro Interceptor (JwtAuthFilter)
Toda la magia ocurre en una clase que hereda de OncePerRequestFilter:
`java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");

    // Si hay un token Bearer, lo capturamos
    if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);

        // Validamos criptograficamente
        if (jwtUtil.esValido(token)) {
            String email = jwtUtil.extraerEmail(token);
            UserDetails user = userDetailsService.loadUserByUsername(email);

            // "Iniciamos la sesion" de forma efimera en Spring Security Context
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
    chain.doFilter(request, response);
}
`

### 4. ConfiguraciÃ³n Central (SecurityConfig.java)
Para que todo funcione, hay que decirle a Spring Security que **apague el manejo de sesiones** y registre nuestro filtro manual:
`java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable) // Innecesario con JWT stateless
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll() // Rutas publicas de login
            .requestMatchers(HttpMethod.GET, "/api/v1/menu/**").permitAll() // Menu publico
            .anyRequest().authenticated() // Lo demas requiere Token
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
`

> ðŸ” **Q&A de Seguridad**
> **Â¿Por que se deshabilita CSRF en una API JWT?**
> CSRF es un ataque que explota la costumbre de los navegadores de enviar cookies de sesion automaticamente. Con JWT, obligamos al FrontEnd a inyectar el header Authorization de forma manual usando Javascript. Como el navegador no inyecta JWTs automaticamente, las APIs Stateless son naturalmente inmunes al CSRF.
> 
> **Â¿Donde van los roles?**
> Dentro del token mismo, en un "Claim" (carga util). Spring lo extrae y lo asocia instantaneamente en el hilo actual, ahorrandonos el valioso tiempo de hacer una query a Postgres solo para preguntar "Que permisos tiene el admin?".

---

## ðŸ›¡ï¸ PASO 07: Autorizacion y Control de Roles

La Autenticacion nos dice **quien** es el usuario. La Autorizacion nos dice **que puede hacer**.
Nuestro sistema de restaurante categoriza a los usuarios en multiples roles, permitiendoles interactuar con el dominio de acuerdo a sus responsabilidades.

### 1. La Matriz de Permisos (RBAC)
- **ROLE_GERENTE**: Control Absoluto (CRUD). Puede crear, editar o eliminar platos y ver estadisticas financieras.
- **ROLE_MESERO**: Rol Operacional. Puede leer el Menu y tomar pedidos.
- **ROLE_COCINERO**: Rol Interno. Solo puede ver los pedidos en la cola y actualizar sus estados.
- **ROLE_CLIENTE**: Rol Publico. Solo puede ver el menu disponible (GET /menu) y gestionar su propio perfil.

### 2. @EnableMethodSecurity (La capa Magica)
Para que los roles puedan controlar el acceso a los *endpoints* a nivel de metodo, comprobamos que tu clase central SecurityConfig.java posee la etiqueta habilitadora:

`java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // ðŸš€ Habilita las anotaciones Pre/Post Authorize en toda la App
public class SecurityConfig {
    // ...
}
`

### 3. @PreAuthorize en los Controllers (Control Granular)
Con la seguridad habilitada, los metodos de la capa Web ahora estan resguardados por un guardia de seguridad que lee los claims del JWT:

`java
@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
public class PlatoController {

    // Cualquier empleado autenticado puede listar los platos internos
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlatoResponseDTO>> listar() { ... }

    // Â¡ALTO AHI! Solo un gerente puede introducir un nuevo plato
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<PlatoResponseDTO> crear(...) { ... }

    // Gerentes o Meseros pueden decir que un plato se acabo en la cocina
    @PatchMapping("/{id}/disponible")
    @PreAuthorize("hasAnyRole('GERENTE', 'MESERO')")
    public ResponseEntity<PlatoResponseDTO> cambiarDisponibilidad(...) { ... }
}
`

> âš ï¸ **El Secreto del Prefijo 'ROLE_'**
> Por convencion, Spring Security asume que un Rol siempre empieza con el prefijo ROLE_ en la base de datos (Ej: ROLE_GERENTE). Al usar la funcion @PreAuthorize("hasRole('GERENTE')"), Spring le antepone la palabra de forma automatica y revisa tus permisos. Es crucial guardar el String correcto en Base de Datos para evitar dolores de cabeza y errores **403**.

### 4. Errores HTTP de Seguridad
El sistema reacciona ante el usuario malicioso de dos formas estandarizadas:

- **401 Unauthorized**: No se proporciono ningun token, o el token JWT esta expirado, corrupto o malformado.
- **403 Forbidden**: El token esta matemÃ¡ticamente intacto y eres un usuario verificado, pero el rol que tienes asignado no esta en la lista de invitados para ese metodo. Esta excepcion (AccessDeniedException) esta capturada en nuestro GlobalExceptionHandler y devuelve un bonito DTO.

---

## ðŸŒ PASO 08: CORS (Cross-Origin Resource Sharing)

Cuando tu aplicacion pasa a Produccion (o incluso en entorno local de React/Next.js), el Frontend y el Backend se ejecutan en "dominios" (Origenes) distintos. El navegador de forma predeterminada **bloqueara por seguridad** cualquier peticion HTTP cross-origin a menos que nuestro backend expida un pase explicito. 

### La Configuracion Global (CorsConfig.java)
Para evitar inyectar anotaciones @CrossOrigin sobre todos los Controllers de la aplicacion (lo que seria una pesadilla de mantenimiento), hemos implementado una resolucion centralizada.

`java
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ðŸš¨ JAMAS uses "*" si setAllowCredentials es true. 
        // El navegador por seguridad destruira la peticion.
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",       // Next.js Dev
            "http://localhost:5173",       // Vite Dev
            "https://bella-ciao.com"       // Produccion
        ));
        
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);  // Vital: Permite enviar JWT en el header de la request

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplica a todos los endpoints
        return source;
    }
}
`

Luego, le indicamos a nuestra aduana principal (SecurityConfig) que cargue esta configuracion en su filtro:
`java
http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
`

### â¸ Q&A de Seguridad
**1. Â¿Cual es la diferencia entre autenticaciÃ³n y autorizaciÃ³n?**
La AutenticaciÃ³n responde a *"Â¿Quien eres?"* comprobando tu token y resolviendo tu identidad.
La Autorizacion responde a *"Â¿Que puedes hacer?"* analizando si el rol de tu identidad alcanza para tocar un boton. 

**2. Â¿Por que el password se guarda con BCrypt y no en texto plano?**
Porque si alguien extrae los datos de la base de datos (Ej: Un DBA malicioso), solo verÃ¡ el Hash matematico (ej. $2a$...) y le tomara siglos decodificar la cadena. Cuando el usuario hace Log-In, Spring jamas desencripta, en su lugar le hace hash al input del usuario y **compara los hashes**.

**3. Â¿CORS lo configura el frontend o el backend?**
Exclusivamente el Backend. El Frontend jamas tiene poder de decision sobre politicas de acceso. Es el navegador leyendo los Headers dictados por Spring Boot (Access-Control-Allow-Origin) quien decide matar o no la peticion antes de entregarla a React/Vite.

---

## ðŸ› ï¸ Ejercicio de Cierre â€” Mapa Mental Multi-BD de tu Restaurante

Como arquitecto de software, la decision mas delicada no es que libreria usar, es **donde** persistir tus datos. A lo largo del proyecto usamos la regla de oro:
- **JPA (PostgresSQL)** para relaciones fuertes, pagos y contabilidad.
- **MongoDB** para velocidad en transacciones simples, logs o catalogos variables.

| Concepto a Implementar | Base de Datos Seleccionada | Justificacion |
| :--- | :--- | :--- |
| **Cuentas y Pagos** | PostgreSQL (JPA) | Requiere ACID (Atomicidad). Una transaccion bancaria no puede perderse. |
| **Mesas y Reservas** | PostgreSQL (JPA) | Relaciones estrictas de horarios. Un Choque de PKs prevendra el doble-booking. |
| **Catalogo de Platos** | PostgreSQL (JPA) | Tablas maestras, referenciadas en los Pedidos mediante llaves foraneas. |
| **Usuarios y Roles** | PostgreSQL (JPA) | Seguridad. Integridad referencial critica. |
| **Historial de Cambios (Menu)** | MongoDB (NoSQL) | Alto volumen y baja relacion. |
| **Logs de Eventos (Auditoria)** | MongoDB (NoSQL) | Formatos de metadatos variables (Mapas). Postgres detestaria esos cambios de estructura. |
| **ReseÃ±as de Clientes** | MongoDB (NoSQL) | Un documento plano con comentarios, puntaje y fecha. |

---
**FIN DE LA SECCION 09 Y FINAL DE AUDITORIA DE CALIDAD DEL PROYECTO.** âœ¨
