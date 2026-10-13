# 02 — Análisis de Requerimientos y Casos de Uso

## 1. Requerimientos Funcionales (RF)

| ID | Requerimiento | Descripción | Patrón / Módulo |
| :--- | :--- | :--- | :--- |
| **RF-BOB-01** | **Validación Encadenada de Turnos** | El sistema debe procesar cada solicitud de turno a través de una cadena secuencial estricta de 5 validaciones: aforo de franja, barbero disponible, datos de cliente, servicio en catálogo y pasarela de pago. Si cualquiera falla, el flujo se detiene de inmediato. | **Chain of Responsibility** (`core.validators`) |
| **RF-BOB-02** | **Normalización Multicanal de Pagos** | El sistema debe procesar pagos a través de 4 medios (Nequi, PSE, Stripe y Efectivo) retornando una respuesta estandarizada `{ paymentId, status, message }` sin que el cliente ni el servicio dependan de contratos heterogéneos. | **Adapter** (`core.services.adapters`) |
| **RF-BOB-03** | **Persistencia Documental en MongoDB** | Toda la información de turnos (aprobados o rechazados), usuarios, servicios y barberos debe ser almacenada en colecciones documentales de MongoDB garantizando trazabilidad y consultas por franja horaria. | **Spring Data MongoDB** (`persistence`) |
| **RF-BOB-04** | **Autenticación y Autorización JWT** | La API debe permitir el registro e inicio de sesión de usuarios emitiendo tokens firmados criptográficamente (HMAC-SHA256) y protegiendo los endpoints de creación y consulta de turnos. | **Spring Security & JJWT** (`config`) |
| **RF-BOB-05** | **Consulta de Catálogo y Disponibilidad** | Los clientes deben poder consultar en tiempo real los servicios activos del catálogo y el equipo de barberos con sus especialidades y horarios disponibles. | **Catalog Service** (`core.services`) |

---

## 2. Requerimientos No Funcionales (RNF)

| ID | Requerimiento | Métrica / Criterio de Aceptación | Componente Afectado |
| :--- | :--- | :--- | :--- |
| **RNF-BOB-01** | **Rendimiento y Latencia** | El sistema debe procesar y responder un turno en tiempo **≤ 2.0 s** para el **95%** de las solicitudes bajo condiciones normales. | Capa Controller, Core y Base de Datos |
| **RNF-BOB-02** | **Disponibilidad Operacional** | Disponibilidad garantizada de al menos el **99.5%** en horario de operación mediante arquitectura de contenedores Docker y Azure App Service. | Infraestructura Docker / Cloud |
| **RNF-BOB-03** | **Seguridad Perimetral y Cero Secretos** | Ninguna contraseña debe almacenarse en texto plano (hashing con BCrypt). Ninguna clave criptográfica ni URI de base de datos debe estar hardcodeada en el código fuente. | Spring Security / Environment Config |
| **RNF-BOB-04** | **Calidad y Cobertura de Código** | Suite automatizada de pruebas unitarias y de integración que verifique los casos de éxito, borde y rechazo con cobertura JaCoCo. | Maven / JaCoCo / JUnit 5 |

---

## 3. Descomposición Ágil: Épica → Feature → Historia de Usuario → Tareas

### Épica:
> **EP-01:** "Gestión Integral y Automatizada del Ciclo de Vida de Turnos y Pagos en Bob's Barbery"

### Feature:
> **FT-01:** "Procesamiento de pagos normalizados con patrón Adapter e integración con Chain of Responsibility"

### Historia de Usuario:
> **US-01:** Pago y Confirmación de Turno con Pasarela Nequi
> - **COMO:** Cliente que solicita una reserva en Bob's Barbery
> - **QUIERO:** Seleccionar Nequi como medio de pago ingresando mi número telefónico
> - **PARA:** Procesar la transacción de forma transparente y obtener mi turno confirmado en la franja seleccionada.

#### Criterios de Aceptación (Gherkin):
```gherkin
Escenario: Pago exitoso con Nequi
  DADO que el cliente solicita el servicio "SER-002" para "Martes 10:00 AM"
  Y el barbero "Miguel" se encuentra disponible
  Y el cliente ingresa un número de teléfono terminado en "65"
  CUANDO el sistema procesa el turno a través de la cadena de validación
  ENTONCES la pasarela NequiAdapter retorna estado "APROBADO" con identificador "NQ-XXXX"
  Y el turno queda registrado en MongoDB con estado "CONFIRMADO" y código "TURNO #001".

Escenario: Pago rechazado con Nequi por teléfono no habilitado
  DADO que el cliente solicita el servicio "SER-002"
  Y el cliente ingresa un número telefónico que no termina en "65"
  CUANDO el sistema ejecuta la validación de pago
  ENTONCES la pasarela NequiAdapter retorna estado "RECHAZADO"
  Y la cadena detiene el procesamiento emitiendo excepción de validación
  Y el turno queda guardado en MongoDB con estado "RECHAZADO".
```

#### Tareas Técnicas Derivadas:
1. **Tarea 1.1:** Diseñar la interfaz `InterfacePaymentMethod` y el DTO normalizado `PagoResultado`.
2. **Tarea 1.2:** Implementar `NequiAdapter` simulando la validación de terminación en 65 y generación de código `NQ-XXXX`.
3. **Tarea 1.3:** Implementar `PaymentValidationHandler` como eslabón final de la cadena de responsabilidad.
4. **Tarea 1.4:** Escribir pruebas unitarias con JUnit 5 validando respuestas aprobadas y rechazadas de `NequiAdapter`.
