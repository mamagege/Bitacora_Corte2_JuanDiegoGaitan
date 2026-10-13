# 05 — Catálogo y Especificación de Endpoints de la API REST

## 1. Resumen de Rutas y Políticas de Acceso

| Método | Endpoint | Descripción | Acceso / Seguridad | Códigos HTTP |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/health` | Verificación de salud y estado del microservicio | Público | 200 |
| `POST` | `/api/v1/auth/register` | Registro de nuevos usuarios | Público | 201, 400, 409 |
| `POST` | `/api/v1/auth/login` | Autenticación y generación de token JWT | Público | 200, 401 |
| `GET` | `/api/v1/catalog/services` | Listado de servicios activos del catálogo | Público | 200 |
| `GET` | `/api/v1/catalog/barbers` | Listado de barberos, especialidades y horarios | Público | 200 |
| `POST` | `/api/v1/turnos` | Creación y procesamiento de turno por cadena | **Requiere JWT Bearer** | 201, 400, 401, 422 |
| `GET` | `/api/v1/turnos` | Listado general de turnos registrados | **Requiere JWT Bearer** | 200, 401 |
| `GET` | `/api/v1/turnos/{id}` | Consulta de turno por ID o `TURNO #XXX` | **Requiere JWT Bearer** | 200, 401, 404 |
| `GET` | `/swagger-ui.html` | Interfaz gráfica interactiva de Swagger UI | Público | 200, 302 |
| `GET` | `/v3/api-docs` | Especificación OpenAPI 3.0 en formato JSON | Público | 200 |

---

## 2. Detalle de Endpoints y Ejemplos de Petición/Respuesta

### 2.1. `POST /api/v1/auth/register`
Permite registrar un nuevo usuario en la base de datos con contraseña codificada con BCrypt.

#### Cuerpo de Solicitud (JSON):
```json
{
  "username": "carlos",
  "password": "carlosPassword123",
  "email": "carlos@gmail.com",
  "role": "USER"
}
```

#### Respuesta Exitosa (HTTP 201 Created):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9VU0VSIiwic3ViIjoiY2FybG9zIiwiZXhwIjoxNzYwMTU1...",
  "type": "Bearer",
  "username": "carlos",
  "role": "ROLE_USER",
  "expiresInMs": 3600000
}
```

---

### 2.2. `POST /api/v1/auth/login`
Autentica credenciales y emite un JWT firmado.

#### Cuerpo de Solicitud (JSON):
```json
{
  "username": "admin",
  "password": "admin123"
}
```

#### Respuesta Exitosa (HTTP 200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9BRE1JTiIsInN1YiI6ImFkbWluIiwiZXhwIjoxNzYwMTU1...",
  "type": "Bearer",
  "username": "admin",
  "role": "ROLE_USER",
  "expiresInMs": 3600000
}
```

---

### 2.3. `POST /api/v1/turnos`
Crea y procesa un turno a través de las 5 validaciones de la cadena de responsabilidad.
**Encabezado obligatorio:** `Authorization: Bearer <TOKEN_JWT>`

#### Escenario 1: Pago exitoso con Nequi (teléfono termina en 65)
```json
{
  "turnoId": "TURNO #001",
  "clientName": "Carlos Ruiz",
  "clientPhone": "3208872165",
  "clientEmail": "carlos@gmail.com",
  "serviceCode": "SER-002",
  "timeSlot": "Martes 10:00 AM",
  "paymentMethod": "NEQUI",
  "paymentMetadata": {
    "phone": "3208872165"
  },
  "amount": 38000
}
```

#### Respuesta Exitosa (HTTP 201 Created):
```json
{
  "id": "67087a3b4e6c1a0012345678",
  "turnoId": "TURNO #001",
  "clientName": "Carlos Ruiz",
  "clientPhone": "3208872165",
  "clientEmail": "carlos@gmail.com",
  "serviceCode": "SER-002",
  "serviceName": "Corte + barba",
  "timeSlot": "Martes 10:00 AM",
  "assignedBarber": "Miguel",
  "paymentMethod": "NEQUI",
  "amount": 38000.0,
  "paymentId": "NQ-7059",
  "paymentStatus": "APROBADO",
  "paymentMessage": "Pago Nequi exitoso",
  "status": "CONFIRMADO",
  "rejectionReason": null,
  "createdAt": "2026-10-10T20:30:00"
}
```

---

#### Escenario 2: Rechazo por teléfono inválido (HTTP 422 Unprocessable Entity)
Petición enviando teléfono `312345` (menos de 10 dígitos):
```json
{
  "timestamp": "2026-10-10T20:31:00",
  "status": 422,
  "error": "Turno Rechazado",
  "message": "Error: teléfono debe tener 10 dígitos",
  "path": "/api/v1/turnos",
  "fieldErrors": null
}
```

---

#### Escenario 3: Transacción PSE con Bancolombia (HTTP 201 Created - Estado PENDIENTE)
```json
{
  "turnoId": "TURNO #003",
  "clientName": "Julián Mesa",
  "clientPhone": "3112145678",
  "clientEmail": "julian@gmail.com",
  "serviceCode": "SER-001",
  "timeSlot": "Sábado 1:00 PM",
  "paymentMethod": "PSE",
  "paymentMetadata": {
    "bank": "BANCOLOMBIA",
    "accountNumber": "123456789"
  },
  "amount": 45000
}
```
**Respuesta:** Retorna `status: "PENDIENTE"`, `paymentStatus: "PENDING"` (código 10), `assignedBarber: "Andres"`.
