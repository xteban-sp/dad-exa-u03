# Aplicación distribuida basada en microservicios — UPeU (DAD)

Sistema de **gestión de talleres** (instructores, alumnos, talleres) construido con
arquitectura de microservicios **por capas**, Spring Boot + Spring Cloud, seguridad JWT,
resiliencia, balanceo y consistencia distribuida. Despliegue reproducible con Docker.

## Arquitectura (7 servicios + 4 bases PostgreSQL)

| Servicio | Puerto | Rol |
|----------|--------|-----|
| ms-istana-config-server | 8888 | Configuración centralizada (Spring Cloud Config) |
| ms-istana-registry-server | 8761 | Registro y descubrimiento (Eureka) |
| ms-istana-api-gateway | 8080 | Puerta de enlace + seguridad JWT + CORS |
| ms-istana-auth | 8084 | Autenticación, usuarios y emisión de JWT |
| ms-istana-gestion-instructor | 8081 | CRUD de instructores |
| ms-istana-gestion-alumno | 8082 | CRUD de alumnos |
| ms-istana-gestion-taller | 8083 | Talleres + composición (Feign) + Saga |

Bases de datos: `instructordb` (5433), `alumnodb` (5434), `tallerdb` (5435), `authdb` (5436).

Atributos de calidad implementados: **resiliencia** (Circuit Breaker / Resilience4j),
**balanceo** (Spring Cloud LoadBalancer), **seguridad** (JWT + roles ADMIN/INSTRUCTOR/ALUMNO),
**consistencia distribuida** (patrón Saga con compensación).

## Requisitos
- Java 21, Maven 3.9+
- Docker Desktop + Docker Compose v2
- IntelliJ IDEA (para ejecución local) y Postman (para pruebas)

---

## Opción A — Ejecución con Docker Compose (recomendada para evaluar)

Desde la carpeta raíz del proyecto:

```bash
docker compose up --build
```

Esto construye los 7 servicios, levanta las 4 bases PostgreSQL y la red `microservices-net`.
La primera vez tarda (descarga dependencias Maven). Cuando todo esté arriba:

- Eureka: http://localhost:8761
- API Gateway: http://localhost:8080
- Swagger de cada servicio: http://localhost:8081/swagger-ui.html (8082, 8083, 8084)

Para detener: `docker compose down` (agrega `-v` para borrar también los datos).

> Los servicios usan `restart: on-failure`: si arrancan antes que el Config Server,
> se reinician solos hasta que esté disponible. Es normal ver algún reinicio al inicio.

---

## Opción B — Ejecución local en IntelliJ

1. Levanta solo las bases de datos con Docker:
   ```bash
   docker run -d --name pg-instructor -e POSTGRES_DB=instructordb -e POSTGRES_PASSWORD=postgres -p 5433:5432 postgres:16
   docker run -d --name pg-alumno     -e POSTGRES_DB=alumnodb     -e POSTGRES_PASSWORD=postgres -p 5434:5432 postgres:16
   docker run -d --name pg-taller     -e POSTGRES_DB=tallerdb     -e POSTGRES_PASSWORD=postgres -p 5435:5432 postgres:16
   docker run -d --name pg-auth       -e POSTGRES_DB=authdb       -e POSTGRES_PASSWORD=postgres -p 5436:5432 postgres:16
   ```
2. Importa cada módulo (`pom.xml`) en IntelliJ.
3. Arranca en este **orden** (cada uno como Spring Boot app):
   1. ms-istana-config-server (8888)
   2. ms-istana-registry-server (8761)
   3. ms-istana-auth (8084)
   4. ms-istana-gestion-instructor (8081)
   5. ms-istana-gestion-alumno (8082)
   6. ms-istana-gestion-taller (8083)
   7. ms-istana-api-gateway (8080)

> En local los servicios se registran por IP loopback (127.0.0.1). En Docker se usa el
> nombre del contenedor (override por variables de entorno en `docker-compose.yml`).

---

## Usuarios de prueba (sembrados por ms-istana-auth)

| Usuario | Password | Rol |
|---------|----------|-----|
| admin | admin123 | ADMIN |
| instructor1 | inst123 | INSTRUCTOR |
| alumno1 | alum123 | ALUMNO |

## Flujo de uso (todo por el Gateway :8080)

1. `POST /api/auth/login` → obtén el JWT.
2. Envía el JWT en `Authorization: Bearer <token>` en las siguientes peticiones.
3. Políticas: lecturas (GET) para cualquier rol; escrituras solo ADMIN; matricular/inscribir
   permitido a ALUMNO.

Endpoints principales:
- `/api/auth/**` (login, register, validate) — público
- `/api/instructores/**`, `/api/alumnos/**`, `/api/talleres/**`
- Compuestos: `/api/talleres/{id}/asignar-instructor/{idInst}`,
  `/api/talleres/{id}/matricular-alumno/{idAlum}`, `/api/talleres/{id}/detalle-completo`

## Pruebas (Postman)
- `DAD_Microservicios.postman_collection.json` — pruebas manuales por fase.
- `DAD_Verificacion_E2E.postman_collection.json` — flujo automático (Run collection) que
  verifica infra, auth, seguridad, CRUD, Feign, balanceo, resiliencia y Saga con asserts.

## Documentación adicional
- `PLAN_PROYECTO.md` — plan y mapeo con el sílabo.
- `GUIA_FASE3.md` … `GUIA_FASE10.md` — guías de verificación por fase.
- `FASE0_BLUEPRINT.md` — decisiones de arquitectura base.

## Perfiles de configuración
El sistema usa el perfil `dev`. La configuración está centralizada en el Config Server
(`ms-istana-config-server/src/main/resources/config-repo/`). Para `test`/`prod` se replican
los archivos `ms-<servicio>-<perfil>.yml` cambiando datasource y parámetros por entorno
(pendiente como mejora; en producción se recomienda externalizar credenciales y usar
migraciones Flyway/Liquibase en lugar de `ddl-auto: update`).
