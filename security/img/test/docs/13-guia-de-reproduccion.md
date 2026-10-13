# 13 — Guía Paso a Paso de Reproducción en Entorno Limpio

Esta guía permite a un evaluador o desarrollador clonar el proyecto y reproducir la compilación, pruebas, ejecución local y contenedores desde cero sin fricción.

---

## 1. Prerrequisitos del Entorno

Asegurarse de tener instaladas las siguientes herramientas en el sistema anfitrión:
- **Java Development Kit (JDK):** Versión 21 (recomendado Eclipse Temurin 21 o OpenJDK 21).
- **Apache Maven:** Versión 3.9 o superior.
- **Docker Desktop o Docker Engine:** Versión 24.x o superior con soporte para Docker Compose.
- **Git:** Para control de versiones.

Verificar instalaciones:
```bash
java -version
mvn -version
docker --version
docker compose version
```

---

## 2. Clonación y Navegación al Proyecto

```bash
git clone <URL_REPOSITORIO>
cd Bobs-Barbery/DOSW_ParcialT2_JuanGaitan
```

---

## 3. Configuración de Variables de Entorno

Copiar la plantilla de ejemplo para crear el archivo local `.env`:
```bash
cp .env.example .env
```
El archivo `.env` viene preconfigurado con valores seguros por defecto para desarrollo local. El archivo `.env` está expresamente excluido del control de versiones en el `.gitignore`.

---

## 4. Ejecución de la Suite Completa de Pruebas

Para ejecutar las 28 pruebas unitarias, de integración y de seguridad con análisis de cobertura JaCoCo:

```bash
mvn clean verify
```

- **Resultado esperado:** `BUILD SUCCESS` (Tests run: 28, Failures: 0, Errors: 0, Skipped: 0).
- **Reporte JaCoCo:** Abrir en el navegador el archivo `target/site/jacoco/index.html`.

---

## 5. Opción A: Ejecución Local con Docker Compose (Recomendada)

Levanta la base de datos MongoDB y el backend Spring Boot interconectados automáticamente:

```bash
docker compose up -d --build
```

### Comprobaciones:
1. **Verificar contenedores activos:**
   ```bash
   docker compose ps
   ```
2. **Consultar logs de inicio del backend:**
   ```bash
   docker compose logs -f app
   ```
3. **Acceder a los endpoints en el navegador:**
   - **Health Check:** [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health)
   - **Catálogo de Servicios:** [http://localhost:8080/api/v1/catalog/services](http://localhost:8080/api/v1/catalog/services)
   - **Catálogo de Barberos:** [http://localhost:8080/api/v1/catalog/barbers](http://localhost:8080/api/v1/catalog/barbers)
   - **Swagger UI Interactivo:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - **Especificación OpenAPI (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 6. Opción B: Ejecución Local con Maven y Mongo en Docker

Si se prefiere ejecutar la aplicación directamente en la terminal:

1. **Iniciar contenedor de MongoDB:**
   ```bash
   docker run -d --name mongo-parcial -p 27017:27017 -e MONGO_INITDB_DATABASE=dosw_parcial mongo:7
   ```

2. **Ejecutar Spring Boot con Maven:**
   - En PowerShell (Windows):
     ```powershell
     $env:MONGODB_URI="mongodb://localhost:27017/dosw_parcial"
     mvn spring-boot:run
     ```
   - En Bash (Linux / macOS):
     ```bash
     export MONGODB_URI="mongodb://localhost:27017/dosw_parcial"
     mvn spring-boot:run
     ```

---

## 7. Verificación de los 3 Escenarios de Prueba del Enunciado

### Paso 1: Autenticarse para obtener el Token JWT
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```
Copiar el valor del atributo `token` recibido.

---

### Paso 2: Ejecutar Escenario #1 — Turno Aprobado con Nequi
```bash
curl -X POST http://localhost:8080/api/v1/turnos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_OBTENIDO>" \
  -d '{
    "turnoId": "TURNO #001",
    "clientName": "Carlos Ruiz",
    "clientEmail": "carlos@gmail.com",
    "clientPhone": "3208872165",
    "serviceCode": "SER-002",
    "timeSlot": "Martes 10:00 AM",
    "paymentMethod": "NEQUI",
    "paymentMetadata": { "phone": "3208872165" },
    "amount": 38000
  }'
```
**Resultado esperado:** HTTP 201 Created. `assignedBarber: "Miguel"`, `status: "CONFIRMADO"`, `paymentStatus: "APROBADO"`.

---

### Paso 3: Ejecutar Escenario #2 — Rechazo por Teléfono Inválido
```bash
curl -X POST http://localhost:8080/api/v1/turnos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_OBTENIDO>" \
  -d '{
    "turnoId": "TURNO #002",
    "clientName": "Ana Lopez",
    "clientEmail": "ana@gmail.com",
    "clientPhone": "312345",
    "serviceCode": "SER-005",
    "timeSlot": "Lunes 10:00 AM",
    "paymentMethod": "PSE",
    "amount": 45000
  }'
```
**Resultado esperado:** HTTP 422 Unprocessable Entity con mensaje `"Error: teléfono debe tener 10 dígitos"`.

---

### Paso 4: Ejecutar Escenario #3 — Pago con PSE Bancolombia
```bash
curl -X POST http://localhost:8080/api/v1/turnos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_OBTENIDO>" \
  -d '{
    "turnoId": "TURNO #003",
    "clientName": "Julián Mesa",
    "clientEmail": "julian@gmail.com",
    "clientPhone": "3112145678",
    "serviceCode": "SER-001",
    "timeSlot": "Sábado 1:00 PM",
    "paymentMethod": "PSE",
    "paymentMetadata": { "bank": "BANCOLOMBIA", "accountNumber": "123456789" },
    "amount": 45000
  }'
```
**Resultado esperado:** HTTP 201 Created. `assignedBarber: "Andres"`, `status: "PENDIENTE"`, `paymentStatus: "PENDING"`.

---

## 8. Diagnóstico y Problemas Comunes

1. **Puerto 8080 o 27017 ocupado:**
   - Verificar procesos activos: `netstat -ano | findstr 8080` (Windows) o `lsof -i :8080` (Linux).
   - Detener contenedores previos: `docker stop $(docker ps -q)`
2. **Error de autenticación JWT 401 en Swagger:**
   - Hacer clic en el botón verde **Authorize** en la esquina superior derecha de Swagger UI.
   - Introducir únicamente el token JWT (sin escribir la palabra "Bearer" manual si el esquema HTTP ya la antepone).
3. **Limpieza completa de recursos:**
   ```bash
   docker compose down -v
   mvn clean
   ```
