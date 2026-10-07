# Correr PostgreSQL en un contenedor
docker run -d `
  --name postgres-restaurante `
  -e POSTGRES_DB=restaurante `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=postgres `
  -p 5432:5432 `
  -v postgres-data:/var/lib/postgresql/data `
  postgres:16-alpine

# Correr MongoDB en un contenedor
docker run -d `
  --name mongo-restaurante `
  -e MONGO_INITDB_ROOT_USERNAME=admin `
  -e MONGO_INITDB_ROOT_PASSWORD=admin `
  -e MONGO_INITDB_DATABASE=restaurante `
  -p 27017:27017 `
  -v mongo-data:/data/db `
  mongo:7
