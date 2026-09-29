# Justificación Técnica: Modelo de Persistencia Híbrida (Políglota)

## 1. Introducción
En el diseño arquitectónico del sistema para el Restaurante Italiano "Bella Ciao", se ha optado por implementar un **Modelo de Persistencia Híbrida o Políglota**, combinando dos paradigmas de bases de datos radicalmente distintos: **PostgreSQL** (Modelo Relacional / Transaccional) y **MongoDB** (Modelo NoSQL / Documental).

Esta decisión de diseño no es arbitraria, sino que responde a la necesidad de separar responsabilidades (CQRS / Event Sourcing) y aplicar el motor de base de datos adecuado para las necesidades intrínsecas de cada flujo de información del negocio.

---

## 2. Persistencia Relacional (PostgreSQL) - El Teorema ACID

### Casos de Uso en el Sistema
PostgreSQL actúa como la base de datos principal o "fuente de la verdad" del estado actual de la operación. Aquí residen entidades como `Plato`, `Mesa`, `Cuenta`, y `Pedido`.

### Justificación Técnica (ACID)
En el contexto de un restaurante, la concurrencia es alta y la exactitud de la facturación es crítica. PostgreSQL garantiza el cumplimiento de las propiedades **ACID** (Atomicidad, Consistencia, Aislamiento, Durabilidad):

1. **Atomicidad:** Al crear un `Pedido` y asignarlo a una `Cuenta`, o ambas inserciones ocurren exitosamente, o ninguna lo hace. Evitamos estados huérfanos (ej. una cuenta que suma dinero por ítems que no se registraron en cocina).
2. **Consistencia (Integridad Referencial):** Las llaves foráneas (`idMesa` dentro de un pedido, `idPlato` dentro de un ítem) garantizan que no se pueda registrar un pedido en una mesa inexistente, ni cobrar un plato que fue eliminado de la base de datos por accidente.
3. **Aislamiento (Isolation):** Previene condiciones de carrera. Si dos meseros intentan "Abrir cuenta" para la misma mesa simultáneamente (RF10/RN-06), PostgreSQL, gestionado a través de `@Transactional` de Spring, bloquea la fila o detecta la concurrencia, asegurando que una mesa tenga exactamente una cuenta activa.

---

## 3. Persistencia No Relacional (MongoDB) - El Teorema BASE

### Casos de Uso en el Sistema
MongoDB es utilizado exclusivamente para la **Auditoría y Trazabilidad (Log de Eventos)** de los cambios de estado de los pedidos (`eventos_pedido`). 

### Justificación Técnica (BASE)
La auditoría histórica de eventos es un mecanismo de escritura continua (Append-only). No requiere bloqueos transaccionales estrictos ni relaciones matemáticas entre tablas. Aquí es donde brilla el paradigma **BASE** (Básicamente Disponible, Estado Suave, Consistencia Eventual):

1. **Flexibilidad de Esquema:** Los eventos de auditoría (`EventoPedidoDocument`) pueden variar en el futuro. Hoy guardamos el "estado anterior" y "estado nuevo", pero mañana podríamos querer almacenar el JSON exacto de la pizza con sus toppings. MongoDB permite evolucionar esta estructura sin ejecutar migraciones lentas (`ALTER TABLE`).
2. **Rendimiento de Escritura (Básicamente Disponible):** Las escrituras asíncronas de eventos en MongoDB (`CompletableFuture.runAsync()`) aseguran que el hilo principal (ej. el mesero oprimiendo el botón de crear pedido) no se bloquee. La alta velocidad de inserción de MongoDB hace que el sistema sea extremadamente resiliente frente a avalanchas de pedidos (Rendimiento RNF2).
3. **Alivio de la Base de Datos Principal:** Si guardásemos el histórico de cada pequeño paso de cada pedido en PostgreSQL, las tablas crecerían de forma exponencial, volviendo lentas las sentencias `JOIN` para operaciones del día a día. Al derivar esta carga histórica a Mongo, conservamos PostgreSQL rápido, indexado y dedicado únicamente al estado "en vivo" del salón.

---

## 4. Conclusión

El uso de persistencia híbrida en el Restaurante "Bella Ciao" resuelve problemas arquitectónicos modernos:
*   **Exactitud donde importa (PostgreSQL):** Finanzas, facturación, y control estricto de mesas.
*   **Escalabilidad donde se requiere (MongoDB):** Volumen inmenso de registros de auditoría y big-data en tiempo real.

Este enfoque dual separa lógicamente el estado actual transaccional frente a la recolección masiva de métricas, cimentando una arquitectura lista para evolucionar hacia microservicios en el futuro.

---

## 5. Cheat Sheet: Bases de Datos Relacionales vs No Relacionales

A continuación, un resumen técnico y comparativo para la rápida toma de decisiones arquitectónicas.

### Bases de Datos Relacionales (SQL)
Se basan en el modelo relacional (álgebra relacional) estructurando los datos en tablas (filas y columnas).

*   **Ejemplos:** PostgreSQL, MySQL, Oracle, SQL Server.
*   **Esquema:** Rígido / Predefinido. Requiere migraciones (`ALTER TABLE`) para modificar la estructura.
*   **Paradigma Principal:** **ACID** (Atomicidad, Consistencia, Aislamiento, Durabilidad).
*   **Relaciones:** Soportan relaciones nativas mediante `Llaves Foráneas (Foreign Keys)` y operaciones complejas `JOIN`.
*   **Escalabilidad:** Principalmente **Vertical** (Scale-up: Añadir más CPU/RAM al servidor principal). Escalar horizontalmente requiere técnicas complejas de particionamiento (Sharding/Clustering).
*   **Casos de uso ideales:**
    *   Sistemas financieros y transaccionales (Pagos, Facturación).
    *   Inventarios críticos y control de stock.
    *   Sistemas donde la consistencia de la data es 100% obligatoria en tiempo real.

### Bases de Datos No Relacionales (NoSQL)
Abarcan diferentes modelos de almacenamiento optimizados para casos específicos (Documental, Clave-Valor, Grafos, Familias de Columnas).

*   **Ejemplos:** MongoDB (Documental), Redis (Clave-Valor), Neo4j (Grafos), Cassandra (Columnar).
*   **Esquema:** Dinámico / Flexible. Los registros (documentos) pueden tener estructuras completamente distintas en la misma colección.
*   **Paradigma Principal:** **BASE** (Básicamente Disponible, Estado Suave, Consistencia Eventual - *Soportado por el Teorema CAP*).
*   **Relaciones:** No existen los `JOIN` nativos (o son ineficientes). Los datos relacionados se suelen **desnormalizar** o **embeber** en un mismo documento.
*   **Escalabilidad:** Principalmente **Horizontal** (Scale-out: Añadir más nodos/servidores básicos al clúster) de forma nativa.
*   **Casos de uso ideales:**
    *   Logs, métricas, auditorías y big data (Append-only).
    *   Catálogos de productos dinámicos o CMS (Sistemas de Gestión de Contenido).
    *   Sistemas que requieren extrema velocidad de lectura/escritura y alta disponibilidad, sacrificando consistencia inmediata.

### ⚖️ Resumen de Decisión (Regla de Oro)
> **Usa SQL** cuando el valor del dato y sus reglas importan más que la velocidad masiva (Integridad > Escalabilidad). <br>
> **Usa NoSQL** cuando la estructura muta rápidamente o el volumen de tráfico/datos superará a un solo servidor (Escalabilidad/Flexibilidad > Integridad).
