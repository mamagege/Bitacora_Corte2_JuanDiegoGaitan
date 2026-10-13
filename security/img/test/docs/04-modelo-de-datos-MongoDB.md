# 04 — Modelo de Datos y Persistencia No Relacional en MongoDB

## 1. Justificación del Modelo Documental frente al Relacional

Para Bob's Barbery, el modelo documental sobre **MongoDB** ofrece ventajas arquitectónicas decisivas frente a esquemas relacionales tradicionales:

1. **Estructura jerárquica autocontenida:** Una cita o turno (`TurnoDocument`) consolida en un único documento atómico los datos del cliente, los metadatos de pago, la información de asignación del barbero y el estado de la transacción. Esto elimina la necesidad de realizar costosas operaciones de `JOIN` entre múltiples tablas durante consultas de alta concurrencia.
2. **Evolución flexible de metadatos:** Las diferentes pasarelas de pago requieren campos heterogéneos (números de cuenta bancaria para PSE, teléfono para Nequi, CVV para Stripe). MongoDB permite modelar estos metadatos mediante un subdocumento dinámico o `Map<String, String>` sin requerir alteraciones al esquema ni columnas nulas dispersas.
3. **Escalabilidad horizontal y compatibilidad Cloud:** Preparado de forma nativa para clústeres gestionados (MongoDB Atlas, Azure Cosmos DB for MongoDB) y despliegue desacoplado en contenedores Docker.

---

## 2. Colecciones y Esquemas Documentales

### 2.1. Colección `turnos` (`TurnoDocument`)
Almacena el ciclo de vida completo de cada turno procesado.

```json
{
  "_id": { "$oid": "660c1ab0e4b0123456789abc" },
  "turnoId": "TURNO #001",
  "clientName": "Carlos Ruiz",
  "clientPhone": "3208872165",
  "clientEmail": "carlos@gmail.com",
  "serviceCode": "SER-002",
  "serviceName": "Corte + barba",
  "timeSlot": "Martes 10:00 AM",
  "assignedBarber": "Miguel",
  "paymentMethod": "NEQUI",
  "paymentMetadata": {
    "phone": "3208872165"
  },
  "amount": 38000.0,
  "paymentId": "NQ-1001",
  "paymentStatus": "APROBADO",
  "paymentMessage": "Pago Nequi exitoso",
  "status": "CONFIRMADO",
  "rejectionReason": null,
  "createdAt": { "$date": "2026-10-10T15:00:00.000Z" }
}
```

#### Índices configurados:
- `turnoId`: Índice único (`@Indexed(unique = true)`).
- `timeSlot`: Índice simple para acelerar la verificación de aforo concurrente.
- `status`: Índice simple para filtros de citas confirmadas vs rechazadas.

---

### 2.2. Colección `usuarios` (`UserDocument`)
Gestiona los accesos del sistema con seguridad criptográfica.

```json
{
  "_id": { "$oid": "660c1ac1e4b0123456789def" },
  "username": "admin",
  "password": "$2a$10$e8Z4k9V/...", 
  "email": "admin@bobsbarber.com",
  "role": "ROLE_ADMIN",
  "createdAt": { "$date": "2026-10-10T12:00:00.000Z" }
}
```

#### Índices configurados:
- `username`: Índice único (`@Indexed(unique = true)`).
- `email`: Índice único (`@Indexed(unique = true)`).

---

### 2.3. Colecciones `servicios` y `barberos`
Mantienen el catálogo persistido y extensible de Bob's Barbery:
- `servicios`: `{ code: "SER-001", name: "Corte clásico", price: 25000.0, specialty: "CORTE" }`
- `barberos`: `{ name: "Miguel", specialties: ["CORTE", "CORTE_BARBA"], days: ["Lunes", ...], startHour: 10, endHour: 17 }`

---

## 3. Consultas Especializadas en `TurnoRepository`

```java
public interface TurnoRepository extends MongoRepository<TurnoDocument, String> {
    Optional<TurnoDocument> findByTurnoId(String turnoId);
    long countByTimeSlotAndStatus(String timeSlot, String status);
    long countByTimeSlot(String timeSlot);
    List<TurnoDocument> findByClientEmail(String clientEmail);
    List<TurnoDocument> findByStatus(String status);
}
```

La consulta `countByTimeSlotAndStatus(slot, "CONFIRMADO")` es el mecanismo de soporte directo para la primera regla de la cadena de responsabilidad: rechazar si ya existen 3 turnos confirmados para esa franja horaria.
