# Semana 10 · DOSW · ECI - CI/CD con GitHub Actions

El código que no se despliega automáticamente se despliega tarde, con errores o simplemente no se despliega.
**CI/CD (Continuous Integration / Continuous Deployment)** convierte cada `push` de tu código en un proceso auditado, automatizado y verificado: compila, ejecuta pruebas, construye la imagen Docker y la despliega en Azure — sin intervención humana directa y sin margen para el error manual.

---

## Paso 1 — Fundamentos (¿Qué es y por qué lo necesitamos?)

Si Docker empacó nuestra API, CI/CD es el robot de la fábrica que toma la caja, verifica que no esté rota y la envía al cliente final.

### CI — Integración Continua (Continuous Integration)
Cada vez que haces un `git push` a GitHub, se dispara un flujo automático en la nube. Este robot:
1. Descarga el código.
2. Ejecuta **todas las pruebas unitarias y de integración**.
3. Compila el código (Maven).
4. Construye la imagen de Docker.

**¿Por qué importa?** Si alguna prueba falla, el equipo recibe una alerta de inmediato. Así evitamos fusionar código roto en la rama principal.

### CD — Despliegue Continuo (Continuous Deployment)
Si la fase de CI es verde y todo pasa con éxito, el CD se encarga de:
1. Iniciar sesión en Docker Hub y subir la nueva imagen (`docker push`).
2. Conectarse a los servidores en la nube (ej. Azure).
3. Reemplazar la versión vieja del contenedor por la nueva, sin apagar el servicio abruptamente.

### El Flujo Completo (De tu PC a Producción)
1. **Developer:** Hace `git push`.
2. **GitHub (Repositorio):** Recibe el código y avisa a Actions.
3. **GitHub Actions (CI Pipeline):** Corre los tests -> Build -> Docker Push.
4. **Azure QA (Ambiente de Pruebas):** El equipo de QA valida que las nuevas funciones sirvan.
5. **Aprobación Manual:** El líder aprueba el pase a producción.
6. **Azure PROD (Producción):** API pública actualizada y disponible para el usuario real.

---

## Paso 2 — Organización y Estructura

GitHub detecta la magia de CI/CD automáticamente si guardamos nuestros "Pipelines" (archivos YAML) en una ruta específica del repositorio: `.github/workflows/`.

### Estructura de nuestro proyecto
```text
restaurante-api/
├── .github/
│   └── workflows/
│       ├── ci-qa.yml          # ← pipeline de CI + despliegue a QA
│       └── ci-prod.yml        # ← pipeline de despliegue a PROD
├── src/
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```
*Cada archivo `.yml` dentro de `workflows/` es un pipeline independiente con reglas de ejecución separadas.*

### Los Dos Ambientes
En la industria, el código casi nunca va directo a los clientes reales. Haremos dos pipelines:

| Ambiente | ¿Cuándo se activa? | Propósito | Aprobación |
| :--- | :--- | :--- | :--- |
| **QA** | Al hacer `push` a rama `main` o `develop` | Validar que el código funciona tras integrarse. Es el parque de juegos interno del equipo. | **Automática** — Sin fricción manual. |
| **PROD** | Al poner un *Tag* de versión `v*.*.*` o manual | API de producción utilizada por clientes reales. Solo llega código que ya sobrevivió a QA. | **Requiere aprobación manual** en GitHub. |

---

## Paso 3 — Configuración Segura (Secrets)

Las credenciales de la base de datos, el JWT secret, las claves de Azure y el token de Docker Hub **nunca** van en el código. GitHub guarda estos valores encriptados como *Secrets* y los inyecta en el pipeline unicamente en el momento en que se ejecuta.

### Configuración en GitHub
Para crear las variables seguras de entorno, se debe navegar a la interfaz gráfica del repositorio:
Settings -> Secrets and variables -> Actions

### Tabla de Secrets Requeridos
A continuación se detalla la configuración que inyectaremos en la nube para que nuestros pipelines tengan permisos de despliegue:

| Secret | Valor | Para qué sirve |
| :--- | :--- | :--- |
| DOCKERHUB_USERNAME | Su usuario de Docker Hub | Login para hacer push de la imagen |
| DOCKERHUB_TOKEN | Token de acceso de Docker Hub | Autenticación segura sin exponer el password principal |
| AZURE_CREDENTIALS | JSON del Service Principal | Permite a GitHub Actions desplegar recursos en Azure |
| JWT_SECRET_QA | Secret JWT para ambiente QA | Distinto del de PROD — cada ambiente tiene el suyo |
| JWT_SECRET_PROD | Secret JWT para ambiente PROD | Más seguro, rotado periódicamente |
| DB_PASSWORD_QA | Password de BD en Azure QA | Credencial de la BD de QA |
| DB_PASSWORD_PROD | Password de BD en Azure PROD | Credencial de la BD de producción |
| AZURE_WEBAPP_NAME_QA | Nombre del App Service de QA | A dónde desplegar en QA |
| AZURE_WEBAPP_NAME_PROD | Nombre del App Service de PROD | A dónde desplegar en PROD |

> **Advertencia Estricta de Seguridad:** Nunca usen el mismo JWT secret ni las mismas credenciales de Base de Datos en QA y PROD. Si QA se compromete por alguna vulnerabilidad, PROD sigue siendo seguro debido a que cada ambiente es totalmente independiente.

### Generación del Token de Docker Hub
No utilizamos nuestra contraseña de inicio de sesión de Docker Hub en GitHub Actions por motivos de seguridad, sino un token de un solo uso.
1. Ir a hub.docker.com -> Account Settings -> Security -> New Access Token
2. **Nombre:** github-actions
3. **Permisos:** Read & Write
4. Copiar el token inmediatamente (solo se muestra una vez) y guardarlo como el secret DOCKERHUB_TOKEN en GitHub.

---

## Paso 4 — Integración Continua (Pipeline de QA)

El Pipeline de QA (Quality Assurance) es el robot principal del ciclo de desarrollo diario. Su trabajo es detectar errores lo antes posible. Se configura para que se dispare automáticamente con cada commit o *Pull Request* hacia las ramas develop o main.

El archivo se crea en .github/workflows/ci-qa.yml y orquesta tres grandes trabajos (Jobs) dependientes entre sí:

### 1. Job de Pruebas (Test)
Es la barrera de contención. Descarga el código y ejecuta Maven.
* Instala Java 21 en la máquina virtual de GitHub.
* Usa un sistema de caché de Maven (~/.m2) para no descargar las dependencias desde cero en cada ejecución, ahorrando valiosos minutos.
* Si algún test falla, **todo el pipeline se detiene** y marca una X roja en GitHub, impidiendo que el código defectuoso pase a la siguiente etapa.

### 2. Job de Construcción (Build & Push)
*Solo se ejecuta si el Job de Pruebas es exitoso.*
* Inicia sesión en Docker Hub utilizando los Secrets configurados.
* Genera una etiqueta dinámica y única (basada en el ID del commit de git, ej. qa-abc1234) para tener trazabilidad.
* Construye la imagen de la API y la sube al repositorio público.

### 3. Job de Despliegue (Deploy QA)
*Solo se ejecuta si la imagen fue exitosamente subida a Docker Hub.*
* Inicia sesión en Azure mediante el Secret AZURE_CREDENTIALS.
* Ordena al servicio *Azure Web App* de QA que descargue la nueva imagen de Docker Hub (:qa-latest).
* Inyecta remotamente las variables de entorno exclusivas para QA (JWT_SECRET_QA y DB_PASSWORD_QA).

> **Nota arquitectónica:** Observen cómo la API no necesita saber si está en tu computador o en Azure. Simplemente lee la variable SPRING_PROFILES_ACTIVE=docker y busca las variables inyectadas.

---

## Paso 5 — Despliegue Controlado (Pipeline PROD)

El pipeline de producción no debe ejecutarse automáticamente con cada cambio. Requiere **aprobación manual** para asegurar que el código ha sido revisado, probado y avalado por el equipo.

Se ha creado el archivo .github/workflows/ci-prod.yml. Este pipeline se diferencia del de QA en que:
1. Solo se dispara cuando creas un *Tag* de versión en Git (Ej: git tag v1.0.0).
2. Requiere que en GitHub navegues a Settings -> Environments -> New environment, crees uno llamado production y actives Required reviewers.
3. El pipeline se detendrá y esperará a que un líder apruebe la ejecución antes de tocar los servidores de Azure.

---

## Paso 6 — Base de Datos en la Nube (Azure)

Para que la API funcione en Azure, las bases de datos ya no pueden estar en tu Docker local. Debes provisionar infraestructura gestionada en la nube:

### Azure Database for PostgreSQL
- **Creación:** En el portal de Azure, busca "Azure Database for PostgreSQL Flexible server". Crea dos servidores (uno para QA y otro para PROD) usando el nivel B1ms (el más económico).
- **Firewall:** En la sección "Networking", permite acceso a servicios de Azure para que tu App Service pueda conectarse.
- **Cadena de Conexión:** Obtén la URL JDBC en Settings -> Connect y guárdala junto con el password como Secrets en GitHub.

### Azure Cosmos DB for MongoDB
- **Creación:** Busca "Azure Cosmos DB for MongoDB" y crea instancias en el nivel Free Tier.
- **Cadena de Conexión:** Copia el Connection String y asegúrate de inyectarlo en tu pipeline como variable de entorno.

---

## Paso 7 — Su API en línea (Azure App Service)

Aquí es donde tu contenedor de Docker correrá en la nube.
- Busca "App Service" en Azure y crea dos servicios con OS Linux (Publicar como Contenedor).
- En Configuration -> Container settings, apunta el App Service hacia tu imagen en Docker Hub (Ej. 	uusuario/restaurante-api:qa-latest).
- Azure se encargará de levantar el contenedor e inyectarle las variables que GitHub Actions le envía durante el despliegue.

---

## Paso 8 — Documentación Visual (Diagrama de Despliegue)

El diagrama final es la prueba arquitectónica de todo el semestre. Debes construirlo manualmente en una herramienta de diagramado (draw.io, Lucidchart) respetando la convención visual de tu equipo.

**Nodos obligatorios a graficar:**
- El repositorio en GitHub y el motor de GitHub Actions.
- El registro de Docker Hub.
- Los App Services de QA y PROD en Azure.
- Las bases de datos relacionales y no relacionales atadas a cada ambiente.
- Líneas de conexión lógicas que indiquen el protocolo (HTTPS, JDBC).
