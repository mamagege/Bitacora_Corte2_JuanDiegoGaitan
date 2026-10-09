# CI/CD y Despliegue en la Nube

Esta sección contiene las evidencias de que nuestra API ha sido automatizada y desplegada exitosamente en ambientes separados de QA y Producción en Microsoft Azure.

## 🔗 URLs Públicas
* **Ambiente de QA:** [https://restaurante-qa.azurewebsites.net/swagger-ui/index.html](https://restaurante-qa.azurewebsites.net/swagger-ui/index.html) *(o tu URL real)*
* **Ambiente de PROD:** [https://restaurante-prod.azurewebsites.net/swagger-ui/index.html](https://restaurante-prod.azurewebsites.net/swagger-ui/index.html) *(o tu URL real)*

## 📸 Evidencias de Ejecución

### 1. Ejecución del Pipeline en Verde
La automatización de GitHub Actions completando exitosamente las fases de pruebas (Test), empaquetado (Build) y Despliegue (Deploy):

`[PEGA TU IMAGEN AQUÍ: Captura de la pestaña Actions en GitHub con todos los chulos verdes]`

### 2. Aprobación Manual en Producción
Evidencia del control humano en el ambiente de producción (Environment Protection Rules):

`[PEGA TU IMAGEN AQUÍ: Captura del botón verde "Review deployments" o "Approved" en GitHub]`

### 3. Trazabilidad en Docker Hub
Etiquetas dinámicas y semánticas creadas de forma automática por el pipeline:

`[PEGA TU IMAGEN AQUÍ: Captura de Docker Hub mostrando tags como qa-abc1234, v1.0.0 y latest]`

### 4. Contenedor en Azure
El contenedor corriendo dentro de la infraestructura de Azure App Service:

`[PEGA TU IMAGEN AQUÍ: Captura del Log Stream de Azure o del contenedor arrancando en la nube]`

### 5. Configuración de Secrets
Listado de variables seguras inyectadas al pipeline:

`[PEGA TU IMAGEN AQUÍ: Captura de la tabla de secrets en Settings de GitHub (sin mostrar los valores reales)]`

## 📐 Arquitectura de Despliegue

La siguiente arquitectura ilustra el flujo de integración continua, registro de imágenes, y la inyección a dos ambientes aislados de Azure (QA y PROD), cada uno con sus respectivas bases de datos independientes.

`[PEGA TU DIAGRAMA AQUÍ: Exportación en alta calidad desde draw.io o Lucidchart]`

### Estrategia de Ambientes
* **QA (Quality Assurance):** Despliegue automático con cada cambio en `main` o `develop`. Se utiliza para que el equipo pruebe integraciones y valide funcionalidades sin afectar a los usuarios reales.
* **PROD (Production):** Despliegue altamente controlado activado únicamente mediante tags de versión. Contiene su propia base de datos aislada y requiere la aprobación explícita de un líder del equipo antes de recibir nuevo tráfico.
