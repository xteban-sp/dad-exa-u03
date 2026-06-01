# Guía de verificación — Fase 5 (Comunicación entre microservicios con OpenFeign)

## ¿Qué se logró?
El `ms-istana-gestion-taller` ahora **llama a los otros microservicios** (instructor y alumno)
usando **OpenFeign**, resolviéndolos por su nombre en Eureka (`lb://`, con balanceo).
Se agregó la entidad `Inscripcion` (taller ↔ alumno) y tres endpoints compuestos:

```
POST /api/talleres/{idTaller}/asignar-instructor/{idInstructor}
POST /api/talleres/{idTaller}/inscribir-alumno/{idAlumno}
GET  /api/talleres/{idTaller}/detalle-completo
```

## 1) Recompila el taller
Se añadió la dependencia `spring-cloud-starter-openfeign` y clases nuevas.
Haz *Reload* del proyecto Maven en IntelliJ para el módulo `ms-istana-gestion-taller`.

## 2) Arranca TODO (orden importante)
1. Config Server (8888)
2. Registry / Eureka (8761)
3. ms-istana-gestion-instructor (8081)
4. ms-istana-gestion-alumno (8082)
5. ms-istana-gestion-taller (8083)
6. API Gateway (8080)

> Feign necesita que **instructor y alumno estén registrados en Eureka** para poder llamarlos.

## 3) Datos previos (créalos primero, vía Gateway :8080)
- POST `/api/instructores` → anota el `id` (ej. 1)
- POST `/api/alumnos` → anota el `id` (ej. 1)
- POST `/api/talleres` → anota el `id` (ej. 1)

(Si usas la colección Postman, ajusta las variables `instructorId`, `alumnoId`, `tallerId`.)

## 4) Probar los endpoints compuestos (carpeta "Fase 5" en Postman)

**Asignar instructor**
`POST http://localhost:8080/api/talleres/1/asignar-instructor/1`
→ 200, el taller queda con `instructorId = 1` (validado vía Feign contra el ms de instructores).

**Inscribir alumno**
`POST http://localhost:8080/api/talleres/1/inscribir-alumno/1`
→ 201, devuelve el detalle con el alumno ya inscrito y el `cupoDisponible` actualizado.

**Detalle completo**
`GET http://localhost:8080/api/talleres/1/detalle-completo`
→ Devuelve el taller + datos del **instructor** + lista de **alumnos inscritos**,
todos traídos en tiempo real desde sus microservicios vía Feign. Ejemplo:
```json
{
  "id": 1,
  "codigo": "TALL-001",
  "nombre": "Microservicios con Spring Cloud",
  "cupoMaximo": 30,
  "instructor": { "id": 1, "nombres": "Benjamin", "especialidad": "Microservicios" },
  "alumnosInscritos": [ { "id": 1, "codigo": "202410001", "nombres": "Fernando" } ],
  "totalInscritos": 1,
  "cupoDisponible": 29
}
```

## 5) Casos de error (evidencia técnica)
- Asignar instructor inexistente: `.../asignar-instructor/999` → **404** (Feign no lo encuentra).
- Inscribir el mismo alumno dos veces → **409** (ya inscrito).
- Inscribir cuando el cupo está lleno → **409** (sin cupo).
- **Apaga ms-istana-gestion-alumno** y haz `inscribir-alumno` → **503 SERVICE_UNAVAILABLE**
  (el taller no puede comunicarse con el ms de alumnos). En la **Fase 6** esto se vuelve
  resiliente con Circuit Breaker y un *fallback* en vez de fallar.

## 6) Checklist de aceptación Fase 5
- [ ] El taller resuelve instructor y alumno por Eureka (no por URL fija).
- [ ] `asignar-instructor` valida vía Feign y asigna.
- [ ] `inscribir-alumno` valida cupo + existencia y registra la inscripción.
- [ ] `detalle-completo` compone datos de los 3 microservicios.
- [ ] Los casos 404/409/503 responden con el `ApiError` estándar.

Con esto cerramos la **Unidad 1** (arquitectura base completa). El siguiente paso es la
**Fase 6 — Resiliencia con Circuit Breaker (Resilience4j)**, ya de la Unidad 2.
