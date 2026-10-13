# 07 — Estrategia de Pruebas y Aseguramiento de la Calidad

## 1. Estrategia de Pruebas por Capas

El proyecto cuenta con una batería integral de **28 pruebas automatizadas** sin fallos ni omisiones, organizadas en la jerarquía de pruebas de la pirámide de software:

```text
src/test/java/edu/dosw/parcial/
│
├── config/
│   └── JwtServiceTest.java                     # 2 pruebas: Generación, firma HS256, expiración y rechazo por usuario
│
├── controller/
│   ├── AuthControllerTest.java                 # 2 pruebas: Endpoint de registro con JWT y login exitoso
│   ├── CatalogControllerTest.java              # 2 pruebas: Consulta de catálogo de servicios y equipo de barberos
│   ├── HealthControllerTest.java               # 1 prueba: Endpoint de salud /api/v1/health retornando UP
│   └── TurnoControllerTest.java                # 2 pruebas: Creación exitosa de turnos y listado general
│
├── core/
│   ├── services/
│   │   ├── TurnoServiceTest.java               # 4 pruebas: Flujo de creación con mock, rechazo de cadena, listado y búsqueda por ID
│   │   └── adapters/
│   │       └── PaymentAdaptersTest.java        # 5 pruebas: Nequi (termina en 65), PSE (Bancolombia, 1-3, otros), Stripe (cvv 555), Efectivo (>=10k) y Factory
│   └── validators/
│       └── ValidationHandlersTest.java         # 7 pruebas: Escenarios 1, 2 y 3 del enunciado, aforo lleno, barbero no disponible, correo no gmail, servicio fuera de catálogo
│
└── persistence/
    └── EntityMappersTest.java                  # 3 pruebas: Mapper OUT bidireccional (Turno, Usuario, Catálogo)
```

---

## 2. Resultados Reales de Ejecución

Comando ejecutado:
```bash
mvn clean test
```

### Resumen del Reporte Surefire:
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running edu.dosw.parcial.config.JwtServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.dosw.parcial.controller.AuthControllerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.dosw.parcial.controller.CatalogControllerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.dosw.parcial.controller.HealthControllerTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.dosw.parcial.controller.TurnoControllerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.dosw.parcial.core.services.adapters.PaymentAdaptersTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.dosw.parcial.core.services.TurnoServiceTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.dosw.parcial.core.validators.ValidationHandlersTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.dosw.parcial.persistence.EntityMappersTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 3. Cobertura de Código con JaCoCo

El plugin `jacoco-maven-plugin` (versión `0.8.12`) está configurado con las metas `prepare-agent` y `report` durante la fase `test` y `verify`.

- Archivo de métricas de cobertura binario: `target/jacoco.exec`
- Reporte navegable HTML generado: `target/site/jacoco/index.html`

Para generar y verificar el reporte completo:
```bash
mvn verify
```
