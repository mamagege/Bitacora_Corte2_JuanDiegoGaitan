# Guía de Configuración y Ejecución de Bases de Datos

Este documento detalla los pasos necesarios para levantar las bases de datos del proyecto (PostgreSQL y MongoDB) en cualquier computadora, así como las credenciales para conectarse a ellas mediante clientes gráficos como DBeaver y MongoDB Compass.

## 1. Requisitos Previos

* Tener instalado **Docker** y **Docker Desktop** (o Docker Engine en Linux).
* Asegurarse de que los puertos `5433` (PostgreSQL) y `27017` (MongoDB) estén libres en la computadora.

## 2. Levantar las Bases de Datos

El proyecto incluye un archivo `docker-compose.yml` en la raíz que orquesta ambas bases de datos junto con sus volúmenes persistentes. Esto significa que si mueves el proyecto a otra computadora, solo necesitas instalar Docker y ejecutar un comando para tener el entorno de base de datos idéntico.

Para iniciar las bases de datos, abre una terminal en la raíz del proyecto y ejecuta:

```bash
docker-compose up -d
```

*El flag `-d` (detached) permite que los contenedores se ejecuten en segundo plano sin bloquear tu terminal.*

Para apagar las bases de datos cuando termines de trabajar:

```bash
docker-compose down
```

## 3. Conexión a PostgreSQL mediante DBeaver

PostgreSQL actúa como la base de datos transaccional (ACID) del restaurante.

1. Abre **DBeaver** y crea una nueva conexión seleccionando **PostgreSQL**.
2. Configura los siguientes parámetros en la pestaña "Main":
   * **Host:** `localhost`
   * **Port:** `5433` *(Importante: usamos 5433 para no entrar en conflicto con instalaciones locales nativas que usen el 5432).*
   * **Database:** `restaurante_db`
   * **Username:** `postgres`
   * **Password:** `password`
3. Haz clic en "Test Connection" para verificar que se conecta correctamente, y luego en "Finish".

## 4. Conexión a MongoDB mediante MongoDB Compass

MongoDB actúa como la base de datos documental/histórica (BASE).

1. Abre **MongoDB Compass**.
2. En la pantalla de inicio ("New Connection"), verás una barra para ingresar la URI de conexión.
3. Pega exactamente lo siguiente:
   ```text
   mongodb://localhost:27017/
   ```
4. Haz clic en **Connect**.
5. Spring Boot se encargará de crear la base de datos `restaurante_historico` automáticamente en cuanto se haga el primer registro histórico.
