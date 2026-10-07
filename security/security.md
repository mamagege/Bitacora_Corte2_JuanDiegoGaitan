# Configuración de Seguridad y Roles - Bella Ciao

## 1. Roles del Sistema

mvn clean package -DskipTests
mvn compile spring-boot:run


Swagger UI: https://localhost:8443/swagger-ui/index.html

## 1. Roles del Sistema (Paso 1)

### 1.1 Conceptos Teóricos
*   **Identidad vs Rol**: La identidad responde a "¿quién eres?" (ej. Juan Pérez). El rol responde a "¿qué cargo ocupas y qué puedes hacer?" (ej. Administrador).
*   **Principio de Mínimo Privilegio**: Práctica de seguridad donde un usuario solo recibe los permisos estrictamente necesarios para cumplir su función, reduciendo el área de impacto en caso de que su cuenta sea comprometida.

### 1.2 Implementación
Basado en el análisis de requerimientos (scope.md y requirements.md), el sistema define cuatro roles principales para el control de acceso:

* **CLIENTE**: Interactúa con la carta, arma pedidos y verifica su cuenta.
* **MESERO**: Gestiona órdenes presenciales, altera pedidos antes de confirmación y es el único autorizado para procesar pagos y cerrar mesas.
* **COCINA**: Operario del tablero Kanban; su responsabilidad es visualizar pedidos entrantes y transicionar sus estados.
* **ADMIN**: Privilegio máximo. Gestiona el catálogo de platos, el inventario, configuración de mesas y visualización de reportes.

## 2. Matriz de Acceso por Endpoints (Paso 2)

### 2.1 Conceptos Teóricos
*   **Matriz de Control de Acceso (ACL)**: Tabla bidimensional que cruza a los sujetos (roles) con los objetos (endpoints HTTP) para determinar si se permite la interacción.
*   **Métodos HTTP como Acciones**: En REST, el recurso (`/platos`) es el objeto, y el verbo HTTP (`GET`, `POST`) define la acción. Una buena matriz no solo bloquea URLs, sino combinaciones de URL + Método.

### 2.2 Implementación de la Matriz
La siguiente tabla traza los permisos necesarios para cada endpoint expuesto por los controladores del proyecto, definiendo las reglas para el `SecurityFilterChain`.

### 2.1 Módulo de Platos (/api/v1/platos)

| Endpoint | Método HTTP | Acceso Permitido | Regla Spring Security | Justificación |
| :--- | :--- | :--- | :--- | :--- |
| `/api/v1/platos` | `GET` | Público | `permitAll()` | La carta debe ser visible para usuarios sin autenticar. |
| `/api/v1/platos/{id}` | `GET` | Público | `permitAll()` | Detalle del plato visible públicamente. |
| `/api/v1/platos` | `POST` | Administrador | `hasRole("ADMIN")` | Solo el admin crea nuevos platos. |
| `/api/v1/platos/{id}` | `PUT` | Administrador | `hasRole("ADMIN")` | Modificación estructural del plato. |
| `/api/v1/platos/{id}/disponible` | `PATCH` | Administrador | `hasRole("ADMIN")` | Bloqueo manual o automático por inventario agotado. |
| `/api/v1/platos/{id}` | `DELETE` | Administrador | `hasRole("ADMIN")` | Desactivación del plato. |

### 2.2 Módulo de Pedidos (/api/v1/pedidos)

| Endpoint | Método HTTP | Acceso Permitido | Regla Spring Security | Justificación |
| :--- | :--- | :--- | :--- | :--- |
| `/api/v1/pedidos` | `GET` | Mesero, Cocina, Admin | `hasAnyRole("MESERO", "COCINA", "ADMIN")` | Listado general operativo. |
| `/api/v1/pedidos/{id}` | `GET` | Cliente, Mesero, Cocina, Admin | `hasAnyRole("CLIENTE", "MESERO", "COCINA", "ADMIN")` | Todos los actores necesitan consultar el detalle de un pedido en curso. |
| `/api/v1/pedidos` | `POST` | Cliente, Mesero | `hasAnyRole("CLIENTE", "MESERO")` | Creación de pedidos autónoma o presencial. |
| `/api/v1/pedidos/{id}` | `PUT` | Cliente, Mesero | `hasAnyRole("CLIENTE", "MESERO")` | Modificación de ítems antes de enviar a cocina. |
| `/api/v1/pedidos/{id}/estado` | `PATCH` | Cocina, Mesero | `hasAnyRole("COCINA", "MESERO")` | Transición de estados (ej. RECIBIDO a PREPARACIÓN) y entrega/cancelación. |
| `/api/v1/pedidos/{id}` | `DELETE` | Mesero, Admin | `hasAnyRole("MESERO", "ADMIN")` | Anulación de un pedido erróneo. |

### 2.3 Módulo de Mesas (/api/v1/mesas)

| Endpoint | Método HTTP | Acceso Permitido | Regla Spring Security | Justificación |
| :--- | :--- | :--- | :--- | :--- |
| `/api/v1/mesas` | `GET` | Cliente, Mesero, Admin | `hasAnyRole("CLIENTE", "MESERO", "ADMIN")` | Visualización de disponibilidad. |
| `/api/v1/mesas/{id}` | `GET` | Cliente, Mesero, Admin | `hasAnyRole("CLIENTE", "MESERO", "ADMIN")` | Detalle de estado de la mesa. |
| `/api/v1/mesas` | `POST` | Administrador | `hasRole("ADMIN")` | Creación de capacidad instalada. |
| `/api/v1/mesas/{id}` | `PUT` | Administrador | `hasRole("ADMIN")` | Reconfiguración de la mesa. |
| `/api/v1/mesas/{id}/estado` | `PATCH` | Mesero, Admin | `hasAnyRole("MESERO", "ADMIN")` | Actualización de ocupación. |
| `/api/v1/mesas/{id}/abrir-cuenta` | `PATCH` | Cliente, Mesero | `hasAnyRole("CLIENTE", "MESERO")` | Inicio de servicio. |
| `/api/v1/mesas/{id}/cerrar-cuenta` | `PATCH` | Mesero | `hasRole("MESERO")` | Exclusivo del mesero tras consolidar el pago. |

### 2.4 Módulo de Cuentas (/api/v1/cuentas)

| Endpoint | Método HTTP | Acceso Permitido | Regla Spring Security | Justificación |
| :--- | :--- | :--- | :--- | :--- |
| `/api/v1/cuentas` | `GET` | Mesero, Admin | `hasAnyRole("MESERO", "ADMIN")` | Auditoría de cuentas activas o cerradas. |
| `/api/v1/cuentas/{id}` | `GET` | Cliente, Mesero, Admin | `hasAnyRole("CLIENTE", "MESERO", "ADMIN")` | Consulta del consumo actual. |
| `/api/v1/cuentas` | `POST` | Cliente, Mesero | `hasAnyRole("CLIENTE", "MESERO")` | Apertura técnica de la cuenta. |
| `/api/v1/cuentas/{id}/actualizar-total`| `PATCH` | Cliente, Mesero | `hasAnyRole("CLIENTE", "MESERO")` | Recálculo transaccional al agregar ítems. |
| `/api/v1/cuentas/{id}/pagar` | `PATCH` | Mesero | `hasRole("MESERO")` | Cierre financiero. Solo el mesero procesa el pago. |

### 2.5 Autenticación en Swagger UI (Pruebas Locales)

Al integrar **Spring Security**, toda la aplicación y la interfaz de Swagger UI están protegidas mediante **Autenticación Básica HTTP**.

Cuando se accede a Swagger (ej. `http://localhost:8080/swagger-ui.html`), el navegador o la propia interfaz despliegan un pop-up estándar solicitando inicio de sesión. 

Dado que se ha implementado un `UserDetailsService` personalizado conectado a la base de datos, **ya no se usa el usuario por defecto de Spring**. Para acceder debes usar:

*   **Usuario:** El `email` de un usuario registrado en la base de datos.
*   **Contraseña:** La contraseña correspondiente a ese usuario (que el sistema internamente validará contra el hash BCrypt).

## 3. Autenticación Simple HTTP (Base64)

La primera capa de seguridad implementada utiliza el estándar HTTP Basic Authentication. Este enfoque exige que el cliente envíe sus credenciales en el encabezado de cada petición HTTP.

### 3.1 Flujo y Mecánica

1. **Petición**: El cliente envía el header `Authorization: Basic [credenciales_base64]` donde el valor codificado corresponde a `usuario:contraseña`.
2. **Decodificación**: El filtro de Spring Security intercepta la petición, extrae y decodifica el header.
3. **Validación**: A través de la implementación de `UserDetailsService`, se recupera el usuario de la base de datos (identificado por el email).
4. **Autenticación**: Se compara la contraseña provista contra el hash almacenado utilizando `BCrypt`.
5. **Resultado**: Si coincide, se otorga acceso según la matriz de roles; en caso contrario, se retorna un error `401 Unauthorized`.

### 3.2 Aspectos Críticos de Implementación

* **Stateless**: El servidor no mantiene información de sesión de los usuarios. Cada petición debe venir acompañada de sus credenciales.
* **Codificación, no cifrado**: Base64 no ofrece seguridad por sí mismo, es solo codificación. En un entorno real es mandatorio el uso de HTTPS (SSL/TLS).
* **Gestión de Contraseñas**: Todas las contraseñas se almacenan procesadas por `BCryptPasswordEncoder`. Nunca se guardan en texto plano en la base de datos.
* **Integración en Swagger**: Se configura OpenAPI para requerir credenciales tipo "basicAuth" a nivel global, permitiendo la prueba de los endpoints protegidos directamente desde la interfaz gráfica.

### 3.3 Inicialización de Datos (Data Seeder)

Para resolver el bloqueo de acceso inicial y facilitar las pruebas, se implementó un componente `DataSeeder` basado en la interfaz `CommandLineRunner`.

* **Propósito**: Garantizar la existencia de usuarios base con distintos roles para probar todos los endpoints del sistema (incluyendo el acceso a Swagger UI).
* **Mecánica**: Al arrancar la aplicación, se evalúa si la tabla de usuarios está vacía. De ser así, se insertan automáticamente cuatro registros iniciales, uno para cada rol.
* **Seguridad**: Las contraseñas son procesadas por `BCryptPasswordEncoder` antes de guardarse en la base de datos.
* **Credenciales de acceso inicial**: 
  * **ADMIN**: `admin@restaurante.com` / `admin123`
  * **MESERO**: `mesero@restaurante.com` / `mesero123`
  * **COCINA**: `cocina@restaurante.com` / `cocina123`
  * **CLIENTE**: `cliente@restaurante.com` / `cliente123`

## 4. Paso 3 — Autenticación con Proveedor Externo (OAuth2 Google)

Se implementó el flujo de **OAuth2** para permitir a los usuarios autenticarse utilizando "Ingresar con Google". En este modelo, el sistema no gestiona ni almacena las contraseñas de estos usuarios; en su lugar, Google confirma la identidad y proporciona un token.

### 4.1 Flujo Completo

1. El usuario navega al endpoint `/oauth2/authorization/google`.
2. Es redirigido automáticamente a la página de login de Google (`accounts.google.com`).
3. El usuario ingresa sus credenciales de Google y autoriza la aplicación.
4. Google redirige de vuelta a nuestro sistema (`/login/oauth2/code/google?code=...`) enviando un código de autorización.
5. El backend (Spring Security) intercambia este código por un `access_token` y un `id_token` en segundo plano.
6. Se ejecuta el `AuthenticationSuccessHandler`, donde extraemos el email y el nombre, permitiéndonos crear o asociar la sesión en el sistema interno y redirigir al frontend.

### 4.2 Configuración y Variables de Entorno

Para no exponer credenciales sensibles en el código fuente, la integración utiliza **variables de entorno**:

* `GOOGLE_CLIENT_ID`: Identificador público de la app en Google Cloud.
* `GOOGLE_CLIENT_SECRET`: Clave secreta proporcionada por Google.

**Estructura recomendada para desarrollo local**:
Se debe crear un archivo `.env` en la raíz del proyecto (este archivo se debe ignorar en git usando `.gitignore`). Spring Boot puede leer variables del sistema; si se usan IDEs como VSCode o IntelliJ, hay plugins (como EnvFile) o configuraciones directas de Spring Boot que cargan el `.env` al arrancar.

### 4.3 Detalles de Implementación en Código

* **Dependencia**: `spring-boot-starter-oauth2-client` en el `pom.xml`.
* **Propiedades**: En `application.yml` se configuró el `registration` de google mapeando las variables de entorno para `client-id` y `client-secret`.
* **SecurityConfig**: Se agregó el método `.oauth2Login()` al `SecurityFilterChain`, definiendo un `AuthenticationSuccessHandler` personalizado para procesar al usuario autenticado y redirigirlo.

## 5. Paso 4 — Autenticación Stateless con JWT

Para estandarizar la seguridad del API REST a nivel mundial, se implementó el modelo de tokens web JSON (JWT). Esto reemplaza el envío constante de credenciales Base64 en favor de un token temporal.

### 5.1 Flujo y Mecánica del JWT

1. **Autenticación Inicial (Login):** El cliente envía sus credenciales (email y password) al endpoint `/api/v1/auth/login`.
2. **Generación:** El `AuthController` valida las credenciales a través del `AuthenticationManager`. Si son correctas, el `JwtUtil` empaqueta la información del usuario (email y rol) junto con una fecha de expiración (1 hora) y lo firma criptográficamente usando un secreto seguro.
3. **Respuesta:** El servidor devuelve el JWT empaquetado en un `TokenResponseDTO`.
4. **Peticiones Subsecuentes:** El cliente envía este token en el header `Authorization: Bearer <token>` para solicitar recursos protegidos.
5. **Interceptación:** El `JwtAuthFilter` intercepta todas las peticiones, extrae el token, verifica su firma criptográfica y, si es válido, carga el usuario en el `SecurityContext` sin consultar la base de datos de nuevo, logrando una arquitectura **Stateless** ultra rápida.

### 5.2 Detalles de Implementación en Código

* **Dependencias**: Se añadieron `jjwt-api`, `jjwt-impl`, y `jjwt-jackson` al `pom.xml`.
* **JwtUtil**: Un componente que abstrae la complejidad de crear y verificar las firmas HMAC-SHA256 utilizando la llave secreta configurada en `application.yml` (`jwt.secret`).
* **JwtAuthFilter**: Filtro personalizado que hereda de `OncePerRequestFilter`. Valida la presencia del prefijo "Bearer " y extrae el payload.
* **AuthController**: Expone el endpoint público para iniciar sesión y emitir los tokens.
* **SecurityConfig**: Se modificó para registrar el `JwtAuthFilter` antes del filtro estándar de contraseñas de Spring Security.
* **Swagger**: Se actualizó `SwaggerConfig` para usar un esquema HTTP tipo "Bearer", permitiendo que los usuarios peguen el JWT y prueben la API directamente desde la interfaz.

## 6. Paso 5 — Autorización y Permisos de Usuarios (RBAC)

Una vez resuelta la autenticación (saber **quién** es el usuario), implementamos la **autorización** (saber **qué** puede hacer) utilizando Control de Acceso Basado en Roles (RBAC).

### 6.1 Enfoque Dual de Seguridad

Se implementaron dos capas de protección para asegurar una defensa en profundidad:

1.  **Protección a nivel de Rutas HTTP (SecurityConfig):**
    Esta es la primera línea de defensa. A través del método `authorizeHttpRequests`, interceptamos las peticiones antes de que lleguen a los controladores.
    *   Ejemplo: `.requestMatchers(HttpMethod.POST, "/api/v1/platos").hasRole("ADMIN")`
    *   *Ventaja*: Centraliza las reglas gruesas de acceso, permitiendo una auditoría rápida de toda la API.

2.  **Protección a nivel de Métodos (@PreAuthorize):**
    Esta es la segunda línea de defensa y ofrece mayor granularidad. Se agregaron anotaciones directamente en los métodos de los controladores (ej. `PlatoController`, `PedidoController`).
    *   Ejemplo: `@PreAuthorize("hasRole('ADMIN')")` encima de `crear()`.
    *   *Ventaja*: Protege la ejecución del código incluso si en el futuro alguien altera el `SecurityConfig` por error, o si el método es invocado desde otro servicio interno.

### 6.2 Manejo de Errores (GlobalExceptionHandler)

Cuando un usuario autenticado intenta acceder a un recurso para el cual no tiene el rol necesario, Spring Security lanza una `AccessDeniedException`.
*   Para evitar que el servidor devuelva el clásico error 500 o un HTML genérico, se agregó un interceptor en el `GlobalExceptionHandler`.
*   **Resultado**: El cliente recibe un JSON limpio con el status HTTP `403 Forbidden` y el mensaje claro: `"No tienes permisos para realizar esta acción"`.

### 6.3 Resumen de Roles por Entidad Principal
*   **Platos**: Lectura pública. Creación, edición y eliminación exclusivas de **ADMIN**.
*   **Pedidos**: Lectura para empleados y clientes. Creación/Edición para **CLIENTE** y **MESERO**. Cambio de estado exclusivo de **COCINA** y **MESERO**.
*   **Mesas**: Gestión completa por **ADMIN**. Apertura de cuentas por **CLIENTE** y **MESERO**.
*   **Cuentas**: Pagos procesados *únicamente* por el **MESERO**.

## 7. Paso 6 — Cifrado en Tránsito (SSL/TLS con HTTPS)

El último eslabón crítico en la seguridad de la API es garantizar que la información sensible, como las credenciales OAuth2 y los tokens JWT, no viajen en texto plano a través de la red. Para esto, se implementó SSL/TLS, migrando la aplicación de HTTP a HTTPS.

### 7.1 Conceptos Teóricos

*   **SSL/TLS**: Protocolo criptográfico que proporciona comunicaciones seguras por una red.
*   **HTTPS**: Versión segura de HTTP, que utiliza SSL/TLS para cifrar el tráfico bidireccional entre el cliente y el servidor, previniendo ataques de tipo intermediario (Man-in-the-Middle).
*   **Certificado Autofirmado**: Para entornos de desarrollo local (`localhost`), utilizamos un certificado generado por nosotros mismos (usando la herramienta `keytool` de Java). Dado que no está firmado por una Entidad Certificadora (CA) externa como Let's Encrypt, los navegadores mostrarán una advertencia inicial, pero el cifrado del canal es exactamente igual de robusto.

### 7.2 Detalles de Implementación en Código

1.  **Generación del Almacén de Claves (Keystore)**: 
    Se utilizó `keytool` para generar un archivo `restaurante.p12` de tipo PKCS12 con encriptación RSA de 2048 bits y validez de 1 año. Este archivo almacena el certificado público y la llave privada del servidor.
2.  **Protección de Secretos (`.env`)**:
    La contraseña para abrir el Keystore (`SSL_KEYSTORE_PASSWORD`) se colocó en el archivo `.env` de manera segura.
3.  **Configuración del Servidor (`application.yml`)**:
    Se actualizó el puerto del servidor de `8080` a `8443` (puerto estándar para HTTPS alternativo). Se configuró el bloque `server.ssl` para apuntar al archivo `restaurante.p12` del `classpath` inyectando la contraseña mediante variables de entorno.
4.  **Gestión de Repositorio (`.gitignore`)**:
    Se añadió explícitamente `*.p12` al archivo `.gitignore`. Por reglas de seguridad estrictas, las llaves privadas jamás deben subirse a sistemas de control de versiones.

### 7.3 Verificación y Acceso

Para acceder a la API (incluyendo el Swagger UI), ahora es **obligatorio** usar el esquema `https://` y el puerto `8443`:
`https://localhost:8443/swagger-ui/index.html`

*(Nota: En navegadores basados en Chromium, habrá que hacer clic en "Avanzado" -> "Continuar a localhost (no seguro)" la primera vez, debido a que es un certificado de desarrollo).*

## 8. Paso 8 — Acceso desde el Frontend (CORS)

Con la API asegurada mediante JWT y HTTPS, surge un nuevo reto cuando aplicaciones cliente (como React o Angular) intentan consumirla desde un dominio distinto (ej. `localhost:3000` llamando a `localhost:8443`). Los navegadores web modernos bloquean estas peticiones automáticamente por la política de mismo origen (Same-Origin Policy). Para permitirlo de forma segura, se implementó CORS (Cross-Origin Resource Sharing).

### 8.1 Conceptos Teóricos

*   **CORS**: Mecanismo que utiliza cabeceras HTTP adicionales para indicar al navegador web que permita a una aplicación web ejecutarse en un origen (dominio) diferente al del servidor origen.
*   **Allowed Origins**: Lista blanca de dominios que tienen permiso de leer los datos de la API. Nunca se debe usar un asterisco `*` en producción si la aplicación maneja autenticación.
*   **Allow Credentials**: Directiva esencial que permite al navegador del cliente enviar cookies o encabezados de autorización (como nuestro `Authorization: Bearer <token>`). Por razones estrictas de seguridad de la W3C, si esto es `true`, los orígenes permitidos deben estar explícitamente declarados (no se permite `*`).

### 8.2 Detalles de Implementación en Código

1.  **Clase CorsConfig**: 
    Se creó un componente dedicado `@Configuration` que expone un bean `CorsConfigurationSource`.
2.  **Configuración Específica**:
    *   **Orígenes**: Se autorizaron explícitamente los puertos de desarrollo frontend estándar (`http://localhost:3000` para React, `http://localhost:5173` para Vite) y un dominio de producción de ejemplo.
    *   **Métodos y Cabeceras**: Se permitieron todos los métodos HTTP REST (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`) y cualquier cabecera.
    *   **Credenciales**: Se habilitó `setAllowCredentials(true)` para que el cliente pueda inyectar el JWT sin ser bloqueado.
3.  **Activación en Spring Security**:
    En la cadena de filtros `SecurityConfig.java`, se inyectó el bean y se activó mediante `.cors(cors -> cors.configurationSource(corsConfigurationSource))`. Esto asegura que el filtro CORS se ejecute *antes* que cualquier filtro de autenticación, procesando correctamente las peticiones preliminares (`OPTIONS` o *preflight requests*) que envían los navegadores.

## 9. Paso 8 — Checklist de Vulnerabilidades (OWASP Top 10)

Para asegurar la robustez de nuestra API ante escenarios del mundo real, se evaluó el código frente al estándar de la industria (OWASP Top 10 para APIs). A continuación, el reporte de mitigación:

*   ✅ **A1 Broken Access Control**: **Protegido**. Se implementó Control de Acceso Basado en Roles (RBAC) dual: mediante rutas en `SecurityConfig` y mediante granularidad con `@PreAuthorize`.
*   ✅ **A2 Cryptographic Failures**: **Protegido**. Todo el tráfico viaja cifrado bajo HTTPS (SSL/TLS). Las contraseñas están hasheadas con algoritmo *BCrypt* de salteo automático, y los JWT se firman con *HS256* con una llave robusta base64.
*   ✅ **A3 Injection**: **Protegido**. La interacción con las bases de datos (PostgreSQL y MongoDB) se realiza exclusivamente a través de la capa de abstracción *Spring Data* (`JpaRepository`, `MongoRepository`), la cual utiliza de forma nativa *Prepared Statements* que evitan inyecciones SQL/NoSQL. No existe concatenación de strings crudos.
*   ✅ **A4 Insecure Design**: **Protegido**. El patrón DTO (Data Transfer Object) se emplea rigurosamente en toda la aplicación. Las entidades (ej. `UsuarioEntity`) jamás se filtran en las respuestas HTTP, evitando el compromiso involuntario de contraseñas.
*   ✅ **A5 Security Misconfiguration**: **Protegido**. El manejo centralizado de excepciones (`GlobalExceptionHandler`) oculta los temidos *Stack Traces* (Error 500) devolviendo mensajes estandarizados. Los secretos de producción (`.env`, `restaurante.p12`) están excluidos explícitamente mediante `.gitignore`.
*   ✅ **A6 Vulnerable and Outdated Components**: **Mitigado**. Se están empleando las versiones estables más recientes de `Spring Boot 3.x` y la dependencia `jjwt 0.12.3`, provenientes de fuentes confiables (Maven Central).
*   ✅ **A7 Identification and Authentication Failures**: **Mitigado proactivamente**.
    *   *Implementación adicional*: Se añadió un mecanismo de protección Anti-Fuerza Bruta (*Rate Limiting*) en el `AuthController`, bloqueando usuarios tras 5 intentos fallidos (`429 Too Many Requests`).
    *   *Implementación adicional*: Se añadieron restricciones a `LoginRequestDTO` (`@Size(min=8)`) para asegurar complejidad de contraseñas.
    *   *Implementación adicional*: `JwtAuthFilter` captura internamente expiraciones y lanza un `401 Unauthorized` explícito en lugar de un error de servidor.
*   ✅ **A8 Software and Data Integrity Failures**: **Protegido**. Cada petición protegida requiere que la firma criptográfica del JWT sea verificada, garantizando que el token no ha sido alterado (Data Tampering).
*   ✅ **A9 Security Logging and Monitoring Failures**: **Mitigado proactivamente**.
    *   *Implementación adicional*: Se integró `SLF4J` a lo largo de las capas de seguridad. El sistema ahora audita explícitamente:
        *   Logins exitosos y fallidos (con contadores de intentos).
        *   Intentos de acceso a rutas bloqueadas por roles (`403 Forbidden`).
        *   Recepción de tokens JWT expirados o corruptos.
*   ✅ **A10 Server-Side Request Forgery (SSRF)**: **No aplica**. La API no provee características que requieran procesar URLs de destino enviadas por el usuario, anulando este vector de ataque.

## 10. Paso 9 — Protección Activa (CSRF, XSS, Clickjacking)

Para completar el blindaje de la API, se mitigaron tres de los ataques web más comunes configurando cabeceras HTTP de seguridad estrictas desde Spring Security.

### 10.1 CSRF (Cross-Site Request Forgery)
*   **Concepto**: Un atacante engaña al navegador de un usuario autenticado para que ejecute acciones no deseadas en la aplicación (ej. hacer clic en un enlace falso que envía una petición POST al servidor).
*   **Estado**: **Inmune por Diseño**. Dado que la API fue construida como *Stateless* (sin estado) para su uso principal con JWT, el token nunca se envía automáticamente por el navegador en peticiones cruzadas (como sí ocurre con las cookies de sesión clásicas). El cliente (Frontend) debe inyectar manualmente el encabezado `Authorization: Bearer <token>`.
*   **Implementación**: Se mantiene desactivado explícitamente mediante `.csrf(csrf -> csrf.disable())` como es la práctica recomendada para APIs REST con JWT.

### 10.2 XSS (Cross-Site Scripting)
*   **Concepto**: Un atacante inyecta scripts maliciosos (ej. JavaScript) que luego se ejecutan en el navegador de otros usuarios.
*   **Estado**: **Protegido**. Se activaron filtros activos a nivel del servidor.
*   **Implementación**: 
    1.  Se inyectó el encabezado **Content Security Policy (CSP)** con la directiva `default-src 'self'; script-src 'self'`, que prohíbe terminantemente al navegador ejecutar scripts provenientes de dominios de terceros no autorizados.
    2.  Se forzó la cabecera `X-XSS-Protection` en `mode=block` (`XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK`), ordenando a los navegadores que bloqueen automáticamente la renderización de la página si detectan un ataque XSS reflejado.

### 10.3 Clickjacking
*   **Concepto**: Un atacante carga la aplicación dentro de un *iframe* transparente u oculto en su propia página web maliciosa, engañando al usuario para que haga clic en botones de la aplicación real pensando que está interactuando con la página del atacante.
*   **Estado**: **Protegido**.
*   **Implementación**: Se configuró la cabecera **X-Frame-Options** en `DENY` (`frameOptions(frame -> frame.deny())`). Esto instruye categóricamente a todos los navegadores web para que nunca permitan que esta API (o Swagger) sea incrustada dentro de un `<frame>`, `<iframe>`, `<embed>` u `<object>` en ninguna otra página de internet.

## 11. Paso 10 — Verificación Final (Checklist de Seguridad)

Para garantizar la fiabilidad del sistema antes de su despliegue, se ejecutó un checklist exhaustivo comprobando el comportamiento esperado frente a diversos escenarios de autenticación, autorización y protección.

### 11.1 Checklist Funcional de la API

| Escenario de Prueba | Acción Realizada | Resultado Esperado | Estado de Implementación |
| :--- | :--- | :--- | :--- |
| **Login exitoso** | `POST /api/v1/auth/login` con `admin@restaurante.com` y password correcto. | Devuelve `200 OK` con un JSON conteniendo el token JWT. | ✅ **Cumplido**. Validado por `AuthController`. |
| **Login fallido** | `POST /api/v1/auth/login` con password incorrecto. | Devuelve `401 Unauthorized` (y a los 5 intentos, `429 Too Many Requests`). | ✅ **Cumplido**. Spring Security rechaza la petición y el Rate Limiter bloquea el abuso. |
| **Petición sin token** | `POST /api/v1/platos` sin cabecera `Authorization`. | Devuelve `401/403` impidiendo la modificación de la carta. | ✅ **Cumplido**. ExceptionTranslationFilter rechaza usuarios anónimos. |
| **Token inválido** | Enviar `Authorization: Bearer token_falso` a un endpoint protegido. | Devuelve `401 Unauthorized` indicando formato inválido. | ✅ **Cumplido**. Capturado explícitamente en `JwtAuthFilter`. |
| **Token expirado** | Esperar que el JWT expire e intentar hacer una petición. | Devuelve `401 Unauthorized`. | ✅ **Cumplido**. `ExpiredJwtException` capturada en `JwtUtil`/`JwtAuthFilter`. |
| **Acceso denegado (Rol)** | Usuario `CLIENTE` intenta `POST /api/v1/platos`. | Devuelve `403 Forbidden` con mensaje "No tienes permisos". | ✅ **Cumplido**. Validado por `GlobalExceptionHandler` y `@PreAuthorize`. |
| **Acceso permitido (Rol)**| Usuario `ADMIN` intenta `POST /api/v1/platos`. | Devuelve `201 Created` con el nuevo plato. | ✅ **Cumplido**. Reglas de `SecurityConfig` permiten el paso. |
| **Swagger Autenticado** | Usar botón *Authorize*, ingresar JWT de ADMIN e invocar API. | Funciona correctamente devolviendo `200/201`. | ✅ **Cumplido**. Esquema `Bearer` configurado en `SwaggerConfig`. |
| **Cabeceras Activas** | Leer encabezados de la respuesta HTTP en Postman/Navegador. | Retorna `X-Frame-Options: DENY`, `X-XSS-Protection`, etc. | ✅ **Cumplido**. Configurado vía `http.headers()` en `SecurityConfig`. |
| **Contraseñas Hash** | Inspeccionar base de datos de PostgreSQL/MongoDB. | Valores como `$2a$10$e/xN...`, nunca texto plano. | ✅ **Cumplido**. Usando `BCryptPasswordEncoder` al crear usuarios. |
| **CORS Configurado** | Realizar un preflight `OPTIONS` desde frontend React. | Responde con cabeceras `Access-Control-Allow-Origin`. | ✅ **Cumplido**. Habilitado globalmente vía `CorsConfig`. |

### 11.2 Verificación Automatizada (MockMvc)

Para que el proyecto posea integración continua fiable (CI/CD), se añadió una capa de pruebas unitarias automatizadas validando las reglas de autorización, asegurando que un *refactor* futuro no rompa la seguridad.

*   **Archivo Creado**: `src/test/java/com/dows/bitacora2/restaurante/security/SecurityTest.java`
*   **Tecnologías**: `@SpringBootTest`, `@AutoConfigureMockMvc`, `MockMvcRequestBuilders`.
*   **Pruebas Implementadas**:
    2.  `crearPlato_conRolCliente_devuelve403()`: Emplea la anotación `@WithMockUser(roles = "CLIENTE")` para simular un usuario logueado pero sin los privilegios de ADMIN, verificando que Spring Security interrumpa el acceso y lance un 403 Forbidden.

---

## 12. RESUMEN GENERAL DEL MÓDULO

La seguridad no es una característica, es una arquitectura en capas. A lo largo de este módulo, transformamos una API "ingenua" en una plataforma de nivel empresarial, superando cada uno de los 10 pasos fundamentales:

1.  **Fundamentos**: Entendimos el negocio y diseñamos una Matriz de Control de Acceso Estricta (Pasos 1 y 2).
2.  **Autenticación**: Pasamos de credenciales inseguras en texto plano (`Basic Auth`, Paso 3) a integrarnos con la nube (`OAuth2`, Paso 3.5), culminando en el estándar mundial de las APIs modernas: **Tokens JWT Stateless** (Paso 4).
3.  **Autorización**: Convertimos nuestra Matriz de papel en código estricto de Spring Security mediante filtros globales (`SecurityConfig`) y seguridad fina a nivel de método (`@PreAuthorize`) (Paso 5).
4.  **Criptografía**: Ciframos la red migrando a **HTTPS/SSL** y blindamos la base de datos aplicando hash a las contraseñas con **BCrypt** (Paso 6).
5.  **Interoperabilidad**: Enseñamos a nuestra API a comunicarse de manera segura con navegadores externos (Frontend) configurando minuciosamente **CORS** (Paso 7).
6.  **Protección Avanzada**: Analizamos la API contra ataques del mundo real (**OWASP Top 10**), bloqueando fuerza bruta (Rate Limiting) y agregando auditoría completa con `SLF4J` (Paso 8). Adicionalmente, forzamos a los navegadores a protegernos inyectando cabeceras defensivas para neutralizar **XSS** y **Clickjacking** (Paso 9).
7.  **Garantía de Calidad**: Respaldamos todas las reglas mediante pruebas unitarias automatizadas con **MockMvc** (Paso 10).

El resultado final es el backend de *Bella Ciao Restaurante*: una fortaleza digital rápida, moderna y auditable.

---

## 📚 CHEAT SHEET PARA EXAMEN DE SPRING SECURITY

| Concepto Clave | Definición Rápida (Para el Examen) | Su Implementación en Código |
| :--- | :--- | :--- |
| **Authentication vs Authorization** | *AuthN* = Validar quién eres (Login). <br> *AuthZ* = Validar qué puedes hacer (Roles). | **AuthN**: `AuthenticationManager`, `JwtAuthFilter`.<br>**AuthZ**: `.hasRole()`, `@PreAuthorize`. |
| **Stateless (Sin Estado)** | El servidor NO guarda sesiones en RAM. Cada petición HTTP contiene todo lo necesario para ser validada. | `SessionCreationPolicy.STATELESS` (Aunque evitamos usarlo explícitamente para no romper OAuth2). |
| **JWT (JSON Web Token)** | Cadena codificada en 3 partes (Header, Payload, Signature) que prueba la identidad y expira sola. | Librería `jjwt`. Se firma usando `Keys.hmacShaKeyFor()`. |
| **Filtro de Seguridad** | Interceptor que actúa sobre la petición HTTP antes de que llegue al Controller. | Clase que hereda de `OncePerRequestFilter` (`JwtAuthFilter`). |
| **SecurityFilterChain** | Cadena de mando donde Spring evalúa las rutas y aplica las defensas antes de procesar el negocio. | `@Bean public SecurityFilterChain filterChain(HttpSecurity http)` |
| **BCrypt** | Algoritmo de hash irreversible con *salt* automático. Impide recuperar la clave original en caso de robo de DB. | `@Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }` |
| **CORS** | Mecanismo que relaja la *Same-Origin Policy* del navegador para que un Frontend en un puerto llame a una API en otro. | `CorsConfig` con `setAllowedOrigins` y `setAllowCredentials(true)`. |
| **CSRF** | Ataque de falsificación de petición. En APIs REST puras con JWT se deshabilita porque el token no viaja solo. | `.csrf(csrf -> csrf.disable())` |
| **X-Frame-Options** | Cabecera HTTP defensiva. Bloquea el *Clickjacking* impidiendo que la API cargue dentro de un `<iframe>`. | `.headers(h -> h.frameOptions(f -> f.deny()))` |
| **MockMvc** | Librería de testing que levanta un contexto simulado de Spring para probar endpoints y respuestas HTTP. | `@AutoConfigureMockMvc`, `mockMvc.perform(post(...))` |
