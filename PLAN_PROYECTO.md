# Plan del Proyecto Integrador — Microservicios por Capas (Unidad 1 y 2)

**Curso:** Desarrollo de Aplicaciones Distribuidas — UPeU
**Dominio:** Gestión de Talleres (instructor / alumno / taller)
**Stack:** Java 21, Spring Boot 3.3.x, Spring Cloud 2023.x, PostgreSQL, Docker, IntelliJ, Postman
**Arquitectura:** Por capas (controller / service / repository / entity / dto / mapper / exception / config)

---

## Cómo trabajaremos

Avanzamos **una fase por mensaje**. En cada fase:
1. Genero/ajusto los archivos de esa fase.
2. Tú la ejecutas en IntelliJ.
3. Verificamos juntos: levantar en Docker + probar en Postman.
4. Marco la fase como cerrada y pasamos a la siguiente.

No se mezcla más de una fase a la vez para que sea reproducible y verificable (criterio del sílabo).

---

## Mapeo Sílabo → Fases

### UNIDAD 1 — Fundamentos de microservicios

| Sesión | Contenido sílabo | Fase del plan | Estado |
|---|---|---|---|
| U1-S2 | Configuración centralizada | Fase 1 — Config Server | ✅ HECHO Y VERIFICADO |
| U1-S3 | Registro y descubrimiento | Fase 2 — Eureka Registry | ✅ HECHO Y VERIFICADO |
| U1-S1 | Arquitectura base: estructura, controllers, DTO, validaciones, manejo de errores, OpenAPI | Fase 3 — Microservicios REST por capas | ✅ HECHO |
| U1-S4 | API Gateway: enrutamiento, filtros, CORS | Fase 4 — API Gateway | ✅ HECHO |
| U1-S5 | Comunicación entre microservicios + fundamentos de seguridad | Fase 5 — OpenFeign (taller compuesto) | ✅ HECHO |
| U1-S6 | **Evaluación Parcial 1** (20/04) | Checkpoint U1 | ⬜ |

### UNIDAD 2 — Atributos de calidad (Resiliencia, Seguridad, Consistencia)

| Sesión | Contenido sílabo | Fase del plan | Estado |
|---|---|---|---|
| U2-S1 | Resiliencia (Circuit Breaker, tolerancia a fallos) | Fase 6 — Resilience4j | ✅ HECHO |
| U2-S2 | Balanceo de carga (múltiples instancias) | Fase 7 — Load Balancer | ✅ HECHO |
| U2-S3 | Seguridad JWT | Fase 8 — Autenticación JWT (ms-auth) | ✅ HECHO |
| U2-S4 | Políticas y filtros de seguridad | Fase 9 — Autorización en Gateway | ✅ HECHO |
| U2-S5 | Patrones de consistencia de datos | Fase 10 — Consistencia distribuida (Saga) | ⬜ Pendiente |
| U2-S6 | **Evaluación Parcial 2** (01/06) | Checkpoint U2 | ⬜ |

### Transversales (imagen + descripción del producto)

| Entregable | Fase del plan | Estado |
|---|---|---|
| Dockerfile por servicio + docker-compose | Fase 11 — Dockerización | ⬜ Pendiente |
| Documentación técnica + colección Postman + evidencias | Fase 12 — Doc & Verificación final | ⬜ Pendiente |

---

## Detalle de cada fase pendiente

### Fase 3 — Microservicios REST por capas (U1-S1)  ← SIGUIENTE
Construir `ms-gestion-instructor`, `ms-gestion-alumno` y `ms-gestion-taller` (base) con:
- Estructura por capas completa (controller, service, repository, entity, dto, mapper, exception, config).
- CRUD REST para cada dominio (rutas del blueprint).
- Validaciones con `@Valid` / Bean Validation.
- Manejo global de errores con `@RestControllerAdvice` y respuesta estándar.
- Documentación OpenAPI/Swagger UI.
- Cliente de Config Server + Eureka client + datasource PostgreSQL.

**Verificación:** levantar config + eureka + cada servicio, probar CRUD en Postman, ver Swagger en `/swagger-ui.html`.

### Fase 4 — API Gateway (U1-S4)
- Rutas a los 3 microservicios vía `lb://` (descubrimiento por Eureka).
- Filtros y `discovery.locator`.
- Configuración CORS centralizada.

**Verificación:** acceder a los 3 servicios pasando solo por `:8080`.

### Fase 5 — Comunicación entre microservicios (U1-S5)
- `OpenFeign` en `ms-gestion-taller` hacia instructor y alumno.
- Endpoints compuestos: asignar-instructor, inscribir-alumno, detalle-completo.
- Fundamentos de seguridad (preparación para U2).

**Verificación:** endpoint compuesto del taller devuelve datos traídos de los otros servicios.

> **Cierre Unidad 1** — entregable EP1: arquitectura base verificada.

### Fase 6 — Resiliencia / Circuit Breaker (U2-S1)
- Resilience4j sobre los clientes Feign del taller.
- Fallbacks y tolerancia a fallos.

**Verificación:** apagar instructor/alumno y comprobar que el taller responde con fallback en vez de fallar.

### Fase 7 — Balanceo de carga (U2-S2)
- Levantar múltiples instancias de un servicio.
- Spring Cloud LoadBalancer + refinar Eureka.

**Verificación:** round-robin entre instancias (ver logs / instance-id).

### Fase 8 — Seguridad JWT (U2-S3)
- Emisión y validación de JWT (login → token).
- Spring Security + filtro de autenticación.

**Verificación:** login devuelve token; endpoint protegido exige `Authorization: Bearer`.

### Fase 9 — Políticas y filtros (U2-S4)
- Filtro JWT centralizado en el Gateway.
- Roles y políticas de autorización por ruta.

**Verificación:** acceso permitido/denegado según rol en Postman.

### Fase 10 — Consistencia distribuida (U2-S5)
- Patrón Saga (orquestación) para operación multi-servicio (ej. inscribir alumno en taller con cupo).
- Acciones de compensación ante fallo.

**Verificación:** simular fallo a mitad de la operación y comprobar el rollback lógico.

> **Cierre Unidad 2** — entregable EP2: atributos de calidad verificados.

### Fase 11 — Dockerización (transversal)
- `Dockerfile` por servicio.
- `docker-compose.yml`: 6 microservicios + 3 PostgreSQL + red `microservices-net`.

**Verificación:** `docker compose up` levanta todo el sistema sin errores.

### Fase 12 — Documentación y verificación final
- README general + diagramas + decisiones técnicas.
- Colección Postman/Insomnia con todas las pruebas.
- Evidencias de ejecución.

---

## Orden de arranque local (runtime)
1. ms-admin-config-server (8888)
2. ms-admin-registry-server (8761)
3. ms-admin-api-gateway (8080)
4. ms-gestion-instructor (8081)
5. ms-gestion-alumno (8082)
6. ms-gestion-taller (8083)
