# SECCIÃ“N CONTROLLER Y VERIFICACIÃ“N DE CALIDAD

## Flujo de una peticiÃ³n â€” De la red al dominio y de vuelta

Antes de escribir una lÃ­nea de cÃ³digo, es vital tener claro quiÃ©n le habla a quiÃ©n y quÃ© objeto pasa por cada frontera. Este es el mapa de la arquitectura:

1. **Cliente (Postman / React)** envÃ­a un JSON Request.
2. El **Controller (`@RestController`)** intercepta el JSON y, con ayuda de Spring, lo valida usando `@Valid`.
3. El JSON validado se convierte en un **`RequestDTO`**.
4. El Controller utiliza un **MapperIn** para traducir ese `RequestDTO` en un **Objeto de Dominio** puro.
5. El Controller delega ese Objeto de Dominio al **Service (`IService` -> `ServiceImpl`)**.
6. El Service aplica la lÃ³gica de negocio (y excepciones tipadas) operando sobre los objetos de negocio, e interactÃºa con el repositorio si es necesario.
7. El Service finaliza su operaciÃ³n y devuelve **otro Objeto de Dominio** al Controller.
8. El Controller usa un **MapperOut** para convertir ese Objeto de Dominio en un **`ResponseDTO`**.
9. Spring convierte el `ResponseDTO` a un JSON Response y se lo entrega al Cliente.

---

## ðŸ”‘ Las 4 reglas que no se negocian

1. âœ… **El Mapper vive en el Controller â€” no en el Service.** El Service recibe y devuelve objetos de dominio puros, nunca DTOs.
2. âœ… **El Service devuelve objetos de dominio â€” nunca un ResponseDTO.** La conversiÃ³n de salida (DTO) es estrictamente trabajo de la capa de presentaciÃ³n (Controller) justo antes de responder.
3. âœ… **El Controller no tiene lÃ³gica de negocio â€”** solo recibe, traduce, delega y responde.
4. âœ… **SegregaciÃ³n de Validaciones:** Las validaciones sintÃ¡cticas o de formato (input) van en el DTO usando *Bean Validation* (`@NotBlank`, `@Positive`). Las validaciones semÃ¡nticas o de negocio ("Â¿El plato ya existe?") van en el Service y lanzan excepciones propias.

> âš ï¸ **Â¿Por quÃ© el Mapper NO va en el Service?**
> Si un Service retorna un `ResponseDTO`, queda acoplado irreversiblemente a la capa web. El dÃ­a que quieras reaprovechar esa misma lÃ³gica de negocio para mandar un PDF, procesar un mensaje de Kafka o usar WebSockets, tendrÃ¡s que reescribir tu Service porque te obliga a recibir/devolver formatos web. El Mapper en el Controller blinda al Service manteniÃ©ndolo agnÃ³stico y universal.

---

## PASO 01: Las clases de dominio (`model/domain`)

El dominio es el nÃºcleo absoluto. Todo lo demÃ¡s (DTOs, Mappers, Services) orbita alrededor de las clases de esta capa. Si el dominio estÃ¡ mal modelado o expone comportamientos indebidos, toda la arquitectura sufrirÃ¡ de "anemia de dominio" o fugas de lÃ³gica.

### Dominio Rico vs Dominio AnÃ©mico

Las clases de dominio representan los conceptos nativos del negocio y operan bajo la premisa del Java puro: sin `@Entity`, sin `@Service` y sin acoplamiento a infraestructuras externas.

En nuestro proyecto, las entidades (`Plato`, `Pedido`, `EstadoPedido`) saben gobernar su propio estado:

```java
// model/domain/Plato.java
public class Plato {
    private Boolean disponible;

    // âœ… Comportamiento de negocio propio del objeto
    // El dominio sabe quÃ© puede hacer â€” sin depender de estado externo
    public boolean estaDisponible() {
        return Boolean.TRUE.equals(disponible);
    }

    // Activa/desactiva sin que el Service tenga que mutar el campo directamente
    public void activar()   { this.disponible = true;  }
    public void desactivar(){ this.disponible = false; }
}
```

### ðŸŒ¿ Â¿QuÃ© SÃ va como mÃ©todo en el dominio?
Comportamiento que el objeto sabe ejecutar evaluando su propio estado interno, sin depender de repositorios, bases de datos o servicios externos.
Ejemplos en nuestro proyecto:
- `estaDisponible()`, `activar()` en `Plato`.
- `puedeTransicionarA()`, `esCancelable()` en el Enum `EstadoPedido`.
- `calcularSubtotal()` en `ItemPedido`.

### ðŸŒ¿ Â¿QuÃ© NO va en el dominio?
- Validaciones de formato sintÃ¡ctico: `public boolean esValido() { return nombre != null; }` (Eso lo hace `@NotBlank` en el RequestDTO).
- Validaciones que cruzan fronteras: `"Â¿El nombre del plato estÃ¡ duplicado?"` (El objeto `Plato` individual no puede ver a los demÃ¡s platos; eso es trabajo del Service consultando al Validator/Repository).

### Estructura de paquetes recomendada y cumplida
- `model/domain/` â†’ Clases de negocio puras + Enums. *(Paso 01)*
- `model/dto/request/` â†’ Lo que entra crudo al API. *(Paso 02)*
- `model/dto/response/` â†’ Lo que se escupe formateado al API. *(Paso 02)*
- `mapper/` â†’ ConversiÃ³n agnÃ³stica entre capas. *(Paso 03)*
- `service/` â†’ Interfaces (contratos). *(Paso 04)*
- `service/impl/` â†’ LÃ³gica dura (implementaciones). *(Paso 04)*
- `controller/` â†’ Endpoints REST y ruteo HTTP. *(Paso 06)*
- `exception/` â†’ Excepciones y traductor Global. *(Paso 07)*

---

## PASO 02: Los DTOs â€” Lo que entra y lo que sale (`model/dto`)

Los Data Transfer Objects (DTOs) son las verdaderas fronteras de tu sistema. El `RequestDTO` valida todo lo que el cliente te envÃ­a. El `ResponseDTO` filtra y maquilla todo lo que le devuelves al cliente.

> ðŸ’¡ **Pregunta guÃ­a antes de crear un DTO:**
> Â¿QuÃ© campos son obligatorios? Â¿CuÃ¡les opcionales? Â¿QuÃ© expongo cuando respondo? La respuesta dictamina cuÃ¡ntos DTOs necesitas. **Nunca utilices un solo DTO global con 15 campos opcionales** para abarcar peticiones diferentes, pues destruirÃ­as la utilidad del patrÃ³n (rompiendo el principio de responsabilidad Ãºnica).

### EvoluciÃ³n en nuestro proyecto: De clases a Records
Aunque comÃºnmente se enseÃ±an como clases con `@Data` de Lombok, nuestro proyecto eleva este estÃ¡ndar y utiliza **Java Records**. Los records proporcionan inmutabilidad nativa sin necesidad de bibliotecas de terceros, lo que los hace perfectos para transferencia de datos:

#### ðŸ“¥ RequestDTO â€” Lo que entra (La Barrera de ValidaciÃ³n)
AquÃ­ es donde se aplican las reglas sintÃ¡cticas usando anotaciones *Bean Validation*:

```java
// model/dto/request/PlatoRequestDTO.java
public record PlatoRequestDTO(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Maximo 100 caracteres")
    String nombre,

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    Double precio,

    @NotBlank(message = "La categoria es obligatoria")
    String categoria,

    // Campo opcional â€” no requiere @NotNull
    String descripcion
) {}
```

#### ðŸ“¤ ResponseDTO â€” Lo que sale (El Filtro de Salida)
```java
// model/dto/response/PlatoResponseDTO.java
public record PlatoResponseDTO(
    Long id,
    String nombre,
    Double precio,
    String categoria,
    Boolean disponible
) {}
```
*(Nota: No exponemos relaciones internas pesadas ni enviamos la clase de Dominio directa, enviamos una proyecciÃ³n curada).*

### âš ï¸ Â¿Por quÃ© el Service no devuelve el ResponseDTO directamente?
Si tu capa de servicio devolviera un `ResponseDTO`, quedarÃ­a contaminada por la infraestructura de transporte web. Si maÃ±ana la app exige exportar un reporte en Excel o procesar mensajes de RabbitMQ, te verÃ­as forzado a modificar el *Service* (donde vive tu lÃ³gica intocable) porque quedÃ³ atado a formatos de respuesta JSON. Es el Controller quien, utilizando el Mapper, convierte el Dominio devuelto por el Service en un ResponseDTO.

### Referencia de Anotaciones Clave
| AnotaciÃ³n | Para quÃ© sirve | Ejemplo PrÃ¡ctico |
|-----------|----------------|------------------|
| `@NotNull` | El campo no puede ser `null` (pero sÃ­ "") | Objetos complejos numÃ©ricos o fechas |
| `@NotBlank`| String con contenido real tras aplicar *trim* | Nombres, contraseÃ±as, categorÃ­as |
| `@NotEmpty`| Colecciones que no pueden estar vacÃ­as | La lista de `ItemPedido` |
| `@Positive`| NÃºmero estrictamente mayor a 0 | Precios, capacidades, cantidades |
| `@Size` | Limita la longitud mÃ¡xima o mÃ­nima | `@Size(max=100)` para un email |
| `@Future` | Fechas que deben ser posteriores a hoy | Horarios de `Reserva` |

---

## PASO 03: El Mapper â€” El traductor entre capas (`mapper/`)

El Mapper es el motor de conversiÃ³n bidireccional. Traduce de `RequestDTO` hacia `Dominio`, y de `Dominio` hacia `ResponseDTO`. 

> ðŸš¨ **Regla de Arquitectura Fundamental:**
> El Mapper **solo** se inyecta y utiliza en la capa del Controller. Nunca en el Service. Reafirmamos: El Service recibe y despacha objetos puros de dominio.

### MapStruct: GeneraciÃ³n automÃ¡tica de cÃ³digo
En lugar de escribir manualmente decenas de lÃ­neas con `plato.setNombre(dto.getNombre())`, utilizamos **MapStruct**. Esta librerÃ­a procesa tus interfaces en tiempo de compilaciÃ³n y autogenera (escribe por ti) el cÃ³digo Java puro necesario para las conversiones, haciÃ©ndolo extremadamente rÃ¡pido en ejecuciÃ³n.

#### ConfiguraciÃ³n CrÃ­tica en Maven (`pom.xml`)
El orden en el plugin del compilador es un asunto de vida o muerte:
**Lombok DEBE procesarse siempre antes que MapStruct.**
Â¿Por quÃ©? Porque MapStruct necesita leer los *getters* y *setters* de las clases para saber cÃ³mo mapear. Si MapStruct se ejecuta primero, se toparÃ¡ con clases vacÃ­as (porque Lombok aÃºn no ha inyectado el cÃ³digo) y la compilaciÃ³n fallarÃ¡ silenciosamente o con errores crÃ­pticos. En nuestro `pom.xml` esto ya estÃ¡ garantizado.

#### La ImplementaciÃ³n del Mapper
```java
// mapper/PlatoMapper.java
@Mapper(componentModel = "spring") 
public interface PlatoMapper {

    // â”€â”€ MapperIn: RequestDTO â†’ Dominio â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Mapping(target = "id", ignore = true)             // ID se ignora en la creaciÃ³n
    @Mapping(target = "disponible", constant = "true") // Regla: Todo plato nuevo nace disponible
    Plato toDomain(PlatoRequestDTO dto);

    // â”€â”€ MapperOut: Dominio â†’ ResponseDTO â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    PlatoResponseDTO toResponse(Plato plato);

    // â”€â”€ Lista: Mapeo masivo en una sola lÃ­nea â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    List<PlatoResponseDTO> toResponseList(List<Plato> platos);
}
```
*`componentModel = "spring"` permite que Spring Boot detecte esta interfaz y la inyecte automÃ¡ticamente con `@Autowired` o vÃ­a constructor en el Controller.*

### Las Anotaciones `@Mapping` mÃ¡s comunes
| Caso de Mapeo | AnotaciÃ³n MapStruct | CuÃ¡ndo se usa |
|---------------|---------------------|---------------|
| **Campos idÃ©nticos** | (Ninguna - es automÃ¡tico) | Mismo nombre y tipo entre origen y destino. |
| **Campos distintos** | `@Mapping(source="x", target="y")` | Cuando el DTO dice `fecha` pero el dominio dice `timestamp`. |
| **Ignorar campos** | `@Mapping(target="id", ignore=true)`| Al mapear un RequestDTO hacia el dominio (el ID aÃºn no existe). |
| **Valores fijos** | `@Mapping(target="activo", constant="true")`| Reglas deterministas al crear un objeto. |
| **Campos anidados** | `@Mapping(source="mesa.numero", target="numMesa")`| Para aplanar el ResponseDTO y ocultar jerarquÃ­as. |

### ðŸŒ¿ El Secreto de MapStruct (`target/generated-sources`)
Si alguna vez dudas de cÃ³mo MapStruct estÃ¡ realizando la conversiÃ³n, abre tu carpeta `target/generated-sources/annotations/.../mapper/PlatoMapperImpl.java`. DescubrirÃ¡s que MapStruct simplemente escribiÃ³ un bloque gigante de cÃ³digo Java tradicional (`new Plato()`, `setNombre()`, *null-checks*, `for` para listas). Leer esa clase generada es el mejor recurso para depurar mapeos rebeldes.

---

## PASO 04: El Service â€” Donde vive la verdadera lÃ³gica (`service/`)

La capa de Servicio es el cerebro de la aplicaciÃ³n. Todo Controller delega su carga al Service. Si algo debe calcularse, verificarse o decidirse, se hace aquÃ­.

> ðŸš¨ **Regla de Oro del Servicio:**
> **Sin DTOs, sin Spring MVC, sin peticiones HTTP.** La Interfaz del Service (`IPlatoService`) y su implementaciÃ³n (`PlatoServiceImpl`) **Ãºnicamente** reciben Objetos de Dominio y devuelven Objetos de Dominio. El servicio es ciego a la red.

### Interfaz e ImplementaciÃ³n
La segregaciÃ³n mediante interfaces (`IPlatoService`) no es opcional. Es lo que permite que un Controller interactÃºe con el negocio sin saber quÃ© base de datos hay por detrÃ¡s (y facilita inmensamente la creaciÃ³n de *Mocks* para pruebas unitarias).

#### EvoluciÃ³n HÃ­brida en nuestro proyecto
Aunque clÃ¡sicamente al iniciar un proyecto la persistencia se simula en un mapa en memoria (`Map<Long, Plato> platos = new ConcurrentHashMap<>()`), nuestro proyecto ya implementÃ³ una persistencia profesional usando **Spring Data JPA** (`PlatoRepository`).

Aun asÃ­, **la lÃ³gica con Streams sigue siendo el nÃºcleo del servicio** para operaciones complejas donde la base de datos entregÃ³ listas gigantes que requieren cruzarse, filtrarse o tabularse (como ya hicimos en el `ResumenDelDia` usando `flatMap` y `groupingBy`).

### Referencia RÃ¡pida â€” El Stream correcto para cada operaciÃ³n
Cuando recibes colecciones o necesitas hacer cÃ¡lculos sofisticados en memoria, el API de *Streams* es insuperable:

| QuÃ© necesitas hacer en el Service | OperaciÃ³n Stream en Java | Resultado |
|-----------------------------------|--------------------------|-----------|
| **Obtener todos como lista** | `.stream().toList()` | `List<T>` |
| **Filtrar por condiciÃ³n** | `.stream().filter(p -> p.activo()).toList()` | `List<T>` |
| **Buscar uno especÃ­fico** | `.stream().filter(...).findFirst().orElseThrow(...)` | `T` o ExcepciÃ³n |
| **Verificar si existe (Short-circuit)** | `.stream().anyMatch(...)` | `boolean` |
| **Ordenar** | `.stream().sorted(Comparator.comparing(...)).toList()`| `List<T>` |
| **Aplanar listas anidadas** | `.stream().flatMap(x -> x.getItems().stream()).toList()` | `List<Item>` |
| **Sumar un campo numÃ©rico** | `.stream().mapToDouble(Item::getPrecio).sum()` | `double` |
| **Agrupar por llave (Diccionario)** | `.stream().collect(Collectors.groupingBy(...))` | `Map<K, List<T>>` |
| **Contar elementos** | `.stream().filter(...).count()` | `long` |
| **Transformar a otro tipo** | `.stream().map(Item::toDomain).toList()` | `List<R>` |

### âš ï¸ Un Service puede depender de otro Service
Uno de los hitos de nuestra arquitectura es la **reutilizaciÃ³n**.
Si `PedidoServiceImpl` necesita saber si un plato existe y estÃ¡ disponible antes de agregarlo al carrito, **jamÃ¡s** inyecta a `PlatoRepository` para buscarlo manualmente, ni duplica la lÃ³gica de negocio. Lo que hace es inyectar la interfaz `IPlatoService` e invocar el mÃ©todo `buscarPorId()`.
> **Cuidado:** Siempre inyecta la interfaz (`IPlatoService`), nunca la implementaciÃ³n concreta (`PlatoServiceImpl`), y ten cuidado con las **dependencias circulares** (Si el Plato llama al Pedido, y el Pedido llama al Plato, Spring Boot crashearÃ¡ en el arranque).

---

## PASO 05: Validaciones (Input vs Negocio) y Utilidades (`validator/` y `util/`)

Mezclar los dos tipos de validaciÃ³n es el error arquitectÃ³nico mÃ¡s comÃºn. Existen dos fronteras claramente marcadas.

### 1. ValidaciÃ³n de INPUT (SintÃ¡ctica)
Responde a preguntas como: *Â¿El formato es correcto? Â¿El campo estÃ¡ nulo? Â¿El nÃºmero es negativo?*
- **DÃ³nde vive:** Exclusivamente en los DTOs (`@NotBlank`, `@Positive`).
- **CÃ³mo se activa:** AÃ±adiendo la anotaciÃ³n `@Valid` justo al lado de `@RequestBody` en los parÃ¡metros del **Controller**.
- **QuÃ© lanza:** Un `400 Bad Request` automÃ¡tico (manejado por nuestro Gerente `GlobalExceptionHandler`).

### 2. ValidaciÃ³n de NEGOCIO (SemÃ¡ntica)
Responde a preguntas como: *Â¿El nombre del plato ya existe en la carta? Â¿La mesa tiene pedidos sin pagar?*
- **DÃ³nde vive:** En la capa Service, o delegada a un componente dedicado llamado **Validator**.
- **CÃ³mo se activa:** Por cÃ³digo explÃ­cito manual dentro del Service.
- **QuÃ© lanza:** Excepciones de negocio personalizadas como `ConflictoException` (`409`) o `ReglaDeNegocioException` (`422`).

#### Â¿Un Validator por Dominio?
Para no ensuciar el cÃ³digo del Service con decenas de `if-else` kilomÃ©tricos, extraemos esa lÃ³gica a componentes que aplican el SRP (Principio de Responsabilidad Ãšnica).
```java
// validator/IPlatoValidator.java
public interface IPlatoValidator {
    void validarNombreUnico(String nombre, Collection<Plato> existentes);
}
```
*Si la validaciÃ³n solo afecta a Plato, va en PlatoValidator. Si afecta a dos dominios, extrÃ¡elo.*

---

## PASO 05-B: Utilidades Compartidas (`util/`)

A lo largo de tu proyecto notarÃ¡s operaciones algorÃ­tmicas que se repiten: limpiar mayÃºsculas/minÃºsculas, formatear fechas de clientes o redondear los cÃ¡lculos de las propinas y el cobro. 
**No dupliques esa lÃ³gica.**

Hemos implementado el paquete `util` que guarda clases de lÃ³gica pura que no le pertenecen a ningÃºn dominio en concreto.
- **`CalculoUtils.java`:** Para redondear decimales (`redondear()`) o aplicar porcentajes de descuentos.
- **`TextoUtils.java`:** Para purificar nombres (`normalizarNombre()`) y comparar cadenas de texto.

> âš ï¸ **Â¿CuÃ¡ndo es una Utility y cuÃ¡ndo es un Validator?**
> Si la lÃ³gica analiza un requerimiento del negocio y **hace explotar** el flujo lanzando una ExcepciÃ³n (ej. "*Toppings excedidos*"), es un **Validator**. 
> Si la lÃ³gica simplemente mastica datos para transformarlos, limpiarlos o calcularlos sin lanzar excepciones (ej. "*pasar a mayÃºsculas*"), es una **Utility**.

### Mapa de Dependencias â€” Â¿QuiÃ©n usa a quiÃ©n?
La cadena de responsabilidades fluye de la siguiente manera:
1. **Controller:** Delega al Service mediante el **Mapper**.
2. **Mapper:** Traduce sin lÃ³gica de negocio.
3. **Service:** Orquesta todo. Opera sobre los objetos del Dominio, se auxilia con **Utils** estÃ¡ticos para cÃ¡lculos matemÃ¡ticos, y delega chequeos pesados al **Validator**.
4. **Dominio:** Los objetos de negocio protegen y gobiernan su propio estado interno.

---

## PASO 06: El Controller â€” Recibe, traduce y delega (`controller/`)

El Controller es la aduana de nuestro sistema. Su trabajo no es pensar, es despachar. Si tienes condicionales de negocio (`if/else`) en un Controller, estÃ¡s rompiendo la arquitectura.

> ðŸŽ® **La Regla del Controller Tonto:**
> 1. Recibe la peticiÃ³n HTTP y valida el formato (`@Valid`).
> 2. Llama al `MapperIn` para traducir el DTO a un Objeto de Dominio.
> 3. Delega el Objeto de Dominio al `Service`.
> 4. Recibe la respuesta (Dominio) del Service.
> 5. Llama al `MapperOut` para traducirlo a un DTO de respuesta.
> 6. Responde con el cÃ³digo de estado HTTP adecuado.

### ImplementaciÃ³n Real (`PlatoController.java`)
Observemos cÃ³mo se implementa este flujo a la perfecciÃ³n en nuestro proyecto:

```java
@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
@Slf4j
public class PlatoController implements PlatoApi {

    // âœ… El Mapper se inyecta AQUÃ â€” jamÃ¡s en el Service
    private final IPlatoService platoService;
    private final PlatoMapper   platoMapper;

    // â”€â”€ POST /api/v1/platos â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Override @PostMapping
    public ResponseEntity<PlatoResponseDTO> crear(@RequestBody @Valid PlatoRequestDTO dto) {
        log.info("POST /api/v1/platos - nombre={}", dto.nombre());

        // 1. MapperIn: RequestDTO â†’ dominio
        Plato plato = platoMapper.toDomain(dto);

        // 2. Service recibe dominio, devuelve dominio
        Plato creado = platoService.crear(plato);

        // 3. MapperOut: dominio â†’ ResponseDTO
        return ResponseEntity
            .status(HttpStatus.CREATED)  // â† 201 Created para creaciones exitosas
            .body(platoMapper.toResponse(creado));
    }

    // â”€â”€ DELETE /api/v1/platos/{id} â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Override @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        platoService.eliminar(id);
        return ResponseEntity.noContent().build();  // â† 204 No Content, sin cuerpo
    }
}
```

### CÃ³digos de Estado HTTP Correctos
Una API profesional se comunica mediante los cÃ³digos semÃ¡nticos de HTTP, no devolviendo un JSON con `{ "status": "error" }` sobre un estatus `200 OK`. 

- `200 OK`: Para respuestas exitosas de lectura (`GET`) o modificaciones generales (`PUT`, `PATCH`).
- `201 CREATED`: Estrictamente para recursos creados con Ã©xito tras un `POST`.
- `204 NO CONTENT`: Para borrados (`DELETE`) donde la operaciÃ³n fue exitosa pero no hay datos que devolver.
- `400 BAD REQUEST`: Activado automÃ¡ticamente por `@Valid` si el JSON de entrada estÃ¡ mal formado.
- `404 NOT FOUND`: Manejado por el `GlobalExceptionHandler` si el ID no existe.
- `409 CONFLICT`: Lanzado por validadores si se intenta duplicar un recurso existente.
- `422 UNPROCESSABLE ENTITY`: Cuando el JSON estÃ¡ perfecto sintÃ¡cticamente, pero rompe una regla de negocio.

---

## PASO 07: Excepciones y el `GlobalExceptionHandler` (`exception/`)

El `GlobalExceptionHandler` es el Gerente del Restaurante. Intercepta todos los problemas (errores y excepciones) lanzados por los Meseros (Controllers/Services) y le explica al cliente quÃ© ocurriÃ³ de una manera uniforme y digna, en lugar de desmayarse y devolver un "500 Internal Server Error" sin contexto.

### 7.1. Las Excepciones del Dominio
Nuestro dominio arroja excepciones personalizadas ricas en significado, blindando la arquitectura:
- `RecursoNoEncontradoException` (Manejado como **404**)
- `ConflictoException` (Manejado como **409** â€” Ej. Platos con nombres duplicados)
- `EstadoInvalidoException` / `ReglaDeNegocioException` (Manejados como **422** â€” Ej. TransiciÃ³n ilegal en un pedido).

### 7.2. El `ErrorResponseDTO` Uniforme
Garantizamos que si el front-end, Angular o Postman consumen nuestra API, *siempre* recibirÃ¡n la misma estructura de error gracias a nuestro Record Factory:

```json
{
  "timestamp": "2026-10-07T16:00:00.123",
  "status": 404,
  "error": "Not Found",
  "message": "No existe Plato con id=99",
  "path": "/api/v1/platos/99"
}
```

### 7.3. La Magia del `@RestControllerAdvice`
Un Ãºnico componente en todo el proyecto que intercepta cada excepciÃ³n lanzada.
```java
@RestControllerAdvice 
@Slf4j
public class GlobalExceptionHandler {

    // 404 â€” Recurso no encontrado (LÃ³gica de Negocio)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RecursoNoEncontradoException ex, HttpServletRequest request) {
        log.warn("RecursoNoEncontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.of(404, "Not Found", ex.getMessage(), request.getRequestURI()));
    }

    // 400 â€” ValidaciÃ³n de input automÃ¡tica (@Valid fallÃ³ en el DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of(400, "Bad Request", mensaje, request.getRequestURI()));
    }

    // 500 â€” El Muro de ContenciÃ³n Final
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenerico(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.of(500, "Internal Server Error", "Error inesperado del servidor", request.getRequestURI()));
    }
}
```

> Porque Spring evalÃºa y selecciona los manejadores en cascada, del mÃ¡s especÃ­fico al mÃ¡s genÃ©rico. Si el manejador genÃ©rico de `Exception` estuviera arriba, secuestrarÃ­a y atraparÃ­a todos los demÃ¡s errores, matando la utilidad de tus excepciones especÃ­ficas personalizadas. Â¡El 500 es siempre tu Ãºltima lÃ­nea de defensa!

---

## PASO 08: Swagger/OpenAPI â€” La doc que se escribe sola (`docs/`)

Hemos integrado **springdoc-openapi** en el `pom.xml`.
Escribir la documentaciÃ³n directamente en los mÃ©todos del Controller los vuelve ilegibles (un mÃ©todo termina con 12 lÃ­neas de anotaciones por cada 3 lÃ­neas de cÃ³digo Java).
Por lo tanto, la arquitectura de este proyecto aÃ­sla la documentaciÃ³n utilizando **Interfaces API**.

### ðŸ“– El PatrÃ³n de DocumentaciÃ³n Aislada
1. Se crea un paquete `docs/` dentro de `controller/`.
2. Se escribe una interfaz pura (ej. `PlatoApi.java`, `MenuApi.java`) que aglomera todas las anotaciones pesadas de `@Tag`, `@Operation` y `@ApiResponses`.
3. El Controller real usa `implements PlatoApi` y **no** necesita tener ninguna de esas anotaciones. Â¡Swagger es lo suficientemente inteligente para ir a leerlas desde la interfaz!

```java
// controller/docs/MenuApi.java â€” La documentaciÃ³n vive aquÃ­
@Tag(name = "Menu", description = "Consulta de la carta - vista del cliente")
public interface MenuApi {

    @Operation(summary = "Ver carta filtrada por categoria")
    @ApiResponse(responseCode = "200", description = "Platos de la categoria")
    ResponseEntity<List<PlatoResponseDTO>> porCategoria(String categoria);
}
```

### ðŸ› ï¸ DiferenciaciÃ³n SemÃ¡ntica: Platos vs. MenÃº
Como parte del cierre de la guÃ­a del Controlador, analizamos un caso de uso clÃ¡sico:
*Â¿Si `Plato` y `Menu` trabajan sobre los mismos datos (platos), deben estar en el mismo Controller?*
**No.** Responden a actores totalmente distintos.

- **`PlatoController` (`/api/v1/platos`):** DiseÃ±ado para el **Gerente/Admin**. Contiene el CRUD destructivo, expone todos los platos (incluso los inactivos) y retorna estados tÃ©cnicos (201, 204).
- **`MenuController` (`/api/v1/menu`):** DiseÃ±ado para el **Cliente/Comensal**. Es de solo lectura. Internamente reutiliza exactamente el mismo `IPlatoService`, pero los resultados pasan por el filtro `.filter(Plato::estaDisponible)`. Si intentas buscar un plato inactivo por ID desde este Controller, obtendrÃ¡s un `404 Not Found` (protegiendo el negocio).

Ambos Controllers mapean de manera distinta y exponen rutas distintas, pero **no duplicamos cÃ³digo en el Service**. Esa es la verdadera maestrÃ­a de delegar la lÃ³gica de negocio lejos del enrutamiento web.

> âœ… **VerificaciÃ³n Final Swagger:**
> Levanta tu servidor Spring Boot y accede a `http://localhost:8080/swagger-ui/index.html`.
> VerÃ¡s claramente separados los bloques de endpoints para "MenÃº" y "Platos", con todos los cÃ³digos de error correctamente catalogados, mientras que tu cÃ³digo base de Java sigue luciendo prÃ­stino y conciso.

---

## PASO 09: Pruebas Unitarias — Confia, pero verifica (src/test/)

La arquitectura que hemos construido es altamente "Testeable" precisamente porque hemos separado las responsabilidades. Al aislar la logica en el Service, Validator y Utils, podemos probar la logica de negocio sin levantar la red (Controller) ni conectarnos a una base de datos real.

> 💡 **La Regla de Oro de las Pruebas Unitarias:**
> Se prueba la **logica** (Service, Validator, Utils). **NO** se prueba la red ni el enrutamiento (Controller) con pruebas unitarias puras; eso se reserva para pruebas de integracion o Swagger/Postman.

### 9.1. Pruebas del Service (@InjectMocks y @Mock)
En el Service probamos el flujo y las decisiones utilizando **Mockito** para controlar las dependencias.

- **@Mock**: Crea un doble controlado de la dependencia (ej. IPlatoValidator o PlatoRepository). No ejecuta su codigo real, sino que nosotros le dictamos que hacer (ej. doThrow(...)).
- **@InjectMocks**: Es la clase real bajo prueba (nuestro PlatoServiceImpl), a la cual se le inyectan los Mocks creados arriba.
- **erify(...)**: Se usa para comprobar que el Service llamo al Validator.
- **Los 5 Escenarios Obligatorios del Service**:
  1. *Happy Path* (Flujo exitoso)
  2. *Recurso No Encontrado* (404 virtual)
  3. *Duplicado/Conflicto* (409 virtual)
  4. *Estado Invalido* (Regla violada)
  5. *Lista Vacia* (No debe explotar con un 
ull pointer)

*(Nota: En nuestro proyecto ya tenemos todo esto rigurosamente cubierto en PlatoServiceImplTest.java)*

### 9.2. Pruebas del Validator (Logica Pura sin Mocks)
El Validator **NO** requiere mocks porque su trabajo es ejecutar reglas algoritmicas directas. Se prueba de forma pura: le pasas datos correctos y verificas que viva; le pasas datos incorrectos y verificas que explote.

`java
class PlatoValidatorTest {
    private final PlatoValidator validator = new PlatoValidator();

    @Test
    void validarNombreUnico_nombreDuplicado_lanzaConflicto() {
        Plato existente = new Plato(); existente.setNombre("Ajiaco");
        List<Plato> existentes = List.of(existente);

        assertThrows(ConflictoException.class, () ->
            validator.validarNombreUnico("AJIACO", existentes));
    }
}
`

### 9.3. Pruebas de Utilities (Casos Limite y Extremos)
De forma analoga al Validator, las Utilities son funciones puras. En nuestro proyecto hemos asegurado nuestro CalculoUtilsTest y TextoUtilsTest para verificar el redondeo, los descuentos, y la normalizacion de mayusculas y espacios extremos.

---

## 🎯 CHECKLIST DEFINITIVO DE CALIDAD

Antes de dar por concluida la implementación de cualquier nuevo dominio en tu Restaurante (ej. Pedidos, Mesas, Cuentas), revisa rigurosamente estos puntos:

- [x] **Dominio (model/domain/)**: Clases de negocio puras. Sin DTOs. Con métodos de comportamiento propio (estaDisponible(), ctivar()). Los Enums gobiernan sus propias transiciones.
- [x] **RequestDTO (model/dto/request/)**: La aduana de entrada. Inmutable (Records). Solo validaciones sintácticas de *Bean Validation* (@NotBlank, @Positive). Cero lógica.
- [x] **ResponseDTO (model/dto/response/)**: El filtro de salida. Cero lógica. Solo contiene los campos que son seguros y relevantes para el cliente.
- [x] **Mapper (mapper/)**: Interfaz con @Mapper(componentModel="spring"). Transforma 	oDomain(), 	oResponse() y listas. **Solo se inyecta en el Controller.**
- [x] **Interfaz Service (service/)**: El contrato agnóstico. Solo recibe y devuelve objetos de Dominio.
- [x] **ServiceImpl (service/impl/)**: El cerebro. @Service, @Slf4j. Usa todo el poder de Java *Streams* para filtrar y operar. No sabe qué es un DTO ni sabe qué es HTTP.
- [x] **Interfaz Validator (alidator/)**: Contrato de validaciones de negocio semánticas. Inyectada en el ServiceImpl.
- [x] **ValidatorImpl (@Component)**: Cada método valida una regla estricta (SRP). Lanza excepciones de negocio si la regla se rompe.
- [x] **Utils (util/)**: Funciones algorítmicas sin estado. Clases inal con constructores private y métodos static. Solo si la lógica es transversal.
- [x] **Controller (controller/)**: Sin lógica de negocio. Usa el patrón: mapperIn -> service -> mapperOut. Usa @Valid. Retorna códigos correctos (200, 201, 204).
- [x] **Excepciones (exception/)**: Semánticas (ConflictoException, RecursoNoEncontradoException). Capturadas uniformemente por el **GlobalExceptionHandler** (con el 500 siempre de último).
- [x] **Swagger (docs/)**: Documentación separada en Interfaces (PlatoApi, MenuApi). @Tag, @Operation, @ApiResponses.
- [x] **Pruebas (src/test/)**: ServiceImpl testeado orquestando con @Mock y @InjectMocks. Validator testeado a pura lógica sin mocks. Utilities testeadas con casos extremos. Los 5 escenarios están cubiertos.

> 🚨 **Los 5 Errores Arquitectónicos Capitales:**
> ❌ El Mapper inyectado en el ServiceImpl (Debe ir en el Controller).
> ❌ El Service recibiendo o devolviendo DTOs (Solo debe ver Dominio).
> ❌ El Controller con condicionales if/else o lógica de negocio.
> ❌ Validaciones de formato y nulidad metidas en el Validator (Pertenecen al DTO con @Valid).
> ❌ Responder un POST con 200 (Debe ser 201) o un DELETE devolviendo JSON (Debe ser 204).

---

## ⏸ Repaso de Cierre (Q&A del Sistema)

**1. ¿Por qué MenuController y PlatoController inyectan el mismo IPlatoService?**
Porque la lógica central es exactamente la misma (ambos interactúan con los Platos del restaurante). Su diferencia es meramente de actor y semántica: el Menú filtra y oculta (Vista Cliente), mientras el PlatoController expone todo con poderes destructivos (Vista Administrador). Crear un MenuService paralelo generaría deuda técnica y duplicación de reglas de negocio.

**2. Un PUT recibe un nombre que ya tiene el propio plato. ¿Debe lanzar 409 Conflicto?**
**No.** En el método ctualizar(), el Validador de nombre único debe excluir del Stream al plato que se está editando mediante un filtro como .filter(p -> !p.getId().equals(id)). Si no filtras el propio plato, el usuario nunca podrá editar el precio de la "Bandeja Paisa" manteniendo su nombre intacto, porque el sistema creerá erróneamente que es un duplicado.

**3. ¿Qué código devuelve un DELETE exitoso y qué body tiene?**
Devuelve **204 No Content** y su body viene completamente vacío: eturn ResponseEntity.noContent().build();. Si devuelves 200 OK o adjuntas un body JSON confirmando el borrado, estás rompiendo el estándar HTTP.

**4. ¿Cuándo se lanza un 409 y cuándo un 422?**
- **409 Conflict:** Indica un choque en el estado del recurso (El nombre que intentas registrar *ya está ocupado*, o la mesa que intentas borrar *tiene una cuenta activa*).
- **422 Unprocessable Entity:** El JSON enviado está perfectamente construido, pero intentar procesarlo viola una regla intrínseca de negocio (Intentar devolver el estado de un pedido de "LISTO" a "RECIBIDO", o agregar toppings excediendo el máximo).
