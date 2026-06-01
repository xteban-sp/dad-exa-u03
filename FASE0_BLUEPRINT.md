# FASE 0 - Blueprint del Proyecto (Arquitectura por Capas)

## 1) Objetivo de la Fase 0

Definir una base comun y consistente para construir la arquitectura de microservicios del curso con:

- Spring Boot + Spring Cloud
- Config Server + Eureka + API Gateway
- Microservicios de negocio por capas (no hexagonal)
- Docker y Docker Compose
- Estandares listos para Unidad 1 y Unidad 2

Esta fase no implementa logica completa de negocio; deja listo el contrato tecnico para construir de forma ordenada en IntelliJ IDEA.

---

## 2) Estructura general del repositorio

```
microservices-arquitectura-capas/
  ms-istana-config-server/
  ms-istana-registry-server/
  ms-istana-api-gateway/
  ms-istana-gestion-instructor/
  ms-istana-gestion-alumno/
  ms-istana-gestion-taller/
  docker-compose.yml
  README.md
```

---

## 3) Versiones recomendadas

- Java 21
- Maven 3.9+
- Spring Boot 3.3.x
- Spring Cloud 2023.x
- Docker Desktop + Docker Compose v2
- IntelliJ IDEA (Community o Ultimate)

---

## 4) Servicios y puertos

- `ms-istana-config-server`: `:8888`
- `ms-istana-registry-server` (Eureka): `:8761`
- `ms-istana-api-gateway`: `:8080`
- `ms-istana-gestion-instructor`: `:8081`
- `ms-istana-gestion-alumno`: `:8082`
- `ms-istana-gestion-taller`: `:8083`

Base de datos sugerida (PostgreSQL, una por servicio):

- `postgres-instructor`: `:5433`
- `postgres-alumno`: `:5434`
- `postgres-taller`: `:5435`

---

## 5) Nombres de aplicaciones (spring.application.name)

- `ms-istana-config-server`
- `ms-istana-registry-server`
- `ms-istana-api-gateway`
- `ms-istana-gestion-instructor`
- `ms-istana-gestion-alumno`
- `ms-istana-gestion-taller`

---

## 6) Estructura por capas (aplica a instructor, alumno, taller)

Paquete base sugerido: `pe.edu.upeu.dad`

```
pe.edu.upeu.dad.<servicio>
  controller/
  service/
  repository/
  entity/
  dto/
  mapper/
  exception/
  config/
```

Reglas:

- `controller`: expone REST, valida DTO, no contiene logica pesada.
- `service`: reglas de negocio y coordinacion entre repositorios/clients.
- `repository`: interfaces JPA.
- `entity`: persistencia.
- `dto`: contrato de entrada/salida.
- `mapper`: conversion entity <-> dto.
- `exception`: excepciones de dominio + `@RestControllerAdvice`.
- `config`: OpenAPI, CORS, seguridad, beans, etc.

---

## 7) Dependencias por servicio

### 7.1 ms-istana-config-server

- `spring-cloud-config-server`
- `spring-boot-starter-actuator`

### 7.2 ms-istana-registry-server

- `spring-cloud-starter-netflix-eureka-server`
- `spring-boot-starter-actuator`

### 7.3 ms-istana-api-gateway

- `spring-cloud-starter-gateway`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-cloud-starter-config`
- `spring-boot-starter-actuator`
- (U2) `spring-boot-starter-security`
- (U2) JWT library (`jjwt`)

### 7.4 ms-istana-gestion-instructor y ms-istana-gestion-alumno

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-actuator`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-cloud-starter-config`
- `springdoc-openapi-starter-webmvc-ui`
- Driver PostgreSQL

### 7.5 ms-istana-gestion-taller (compuesto)

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-actuator`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-cloud-starter-config`
- `spring-cloud-starter-openfeign`
- `springdoc-openapi-starter-webmvc-ui`
- Driver PostgreSQL
- (U2) `spring-cloud-starter-circuitbreaker-resilience4j`

---

## 8) Contratos API base (primera version)

### 8.1 ms-istana-gestion-instructor

Base path: `/api/instructores`

- `GET /api/instructores`
- `GET /api/instructores/{id}`
- `POST /api/instructores`
- `PUT /api/instructores/{id}`
- `DELETE /api/instructores/{id}`

### 8.2 ms-istana-gestion-alumno

Base path: `/api/alumnos`

- `GET /api/alumnos`
- `GET /api/alumnos/{id}`
- `POST /api/alumnos`
- `PUT /api/alumnos/{id}`
- `DELETE /api/alumnos/{id}`

### 8.3 ms-istana-gestion-taller

Base path: `/api/talleres`

- `GET /api/talleres`
- `GET /api/talleres/{id}`
- `POST /api/talleres`
- `PUT /api/talleres/{id}`
- `DELETE /api/talleres/{id}`

Endpoints compuestos:

- `POST /api/talleres/{idTaller}/asignar-instructor/{idInstructor}`
- `POST /api/talleres/{idTaller}/inscribir-alumno/{idAlumno}`
- `GET /api/talleres/{idTaller}/detalle-completo`

---

## 9) Modelo inicial de datos (simple)

### Instructor

- `id` (Long)
- `dni` (String, unique)
- `nombres` (String)
- `apellidos` (String)
- `especialidad` (String)
- `email` (String, unique)
- `estado` (Boolean)

### Alumno

- `id` (Long)
- `codigo` (String, unique)
- `nombres` (String)
- `apellidos` (String)
- `email` (String, unique)
- `ciclo` (Integer)
- `estado` (Boolean)

### Taller

- `id` (Long)
- `codigo` (String, unique)
- `nombre` (String)
- `descripcion` (String)
- `cupoMaximo` (Integer)
- `instructorId` (Long)
- `estado` (Boolean)

Nota: para mantener bajo acoplamiento entre microservicios, `taller` guarda referencias por ID y obtiene detalle via Feign.

---

## 10) Estandar de respuestas y errores

### Respuesta exitosa sugerida

```json
{
  "timestamp": "2026-06-01T18:45:00Z",
  "message": "Operacion exitosa",
  "data": {}
}
```

### Error sugerido

```json
{
  "timestamp": "2026-06-01T18:45:00Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Instructor no encontrado",
  "path": "/api/instructores/99"
}
```

---

## 11) Configuracion y perfiles

Perfiles: `dev`, `test`, `prod`.

Repositorio de configuraciones centralizadas (se definira en Fase 1):

- `ms-istana-gestion-instructor-dev.yml`
- `ms-istana-gestion-alumno-dev.yml`
- `ms-istana-gestion-taller-dev.yml`
- `ms-istana-api-gateway-dev.yml`

Buenas practicas:

- Nunca hardcodear URLs entre microservicios.
- Resolver por nombre de servicio en Eureka.
- Externalizar credenciales/parametros por environment.

---

## 12) Orden de inicio local (runtime)

1. `ms-istana-config-server`
2. `ms-istana-registry-server`
3. `ms-istana-api-gateway`
4. `ms-istana-gestion-instructor`
5. `ms-istana-gestion-alumno`
6. `ms-istana-gestion-taller`

---

## 13) Docker (criterio base)

Cada microservicio tendra:

- `Dockerfile`
- Variables de entorno para perfil y datasource
- Dependencias declaradas en `docker-compose.yml`

`docker-compose.yml` central tendra:

- 6 microservicios
- 3 bases PostgreSQL
- red comun (`microservices-net`)

---

## 14) Checklist de aceptacion de Fase 0

- Arquitectura definida por capas (controller, service, repository, entity, dto, mapper, exception, config).
- Nombres de servicios y puertos definidos.
- Contratos REST iniciales definidos.
- Modelo base de datos inicial definido.
- Orden tecnico de implementacion definido.
- Preparado para iniciar Fase 1 (Config Server).

---

## 15) Siguiente paso inmediato

Ejecutar Fase 1: implementar `ms-istana-config-server` con repositorio de configuraciones y perfil `dev`.
