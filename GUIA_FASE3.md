# Guía de ejecución y pruebas — Fase 3 (Microservicios REST por capas)

## 1) Requisito previo: bases de datos PostgreSQL

Los 3 servicios usan una base Postgres cada uno (según tu `config-repo`). Como ya usas
Docker, levántalas rápido con estos comandos (coinciden con puertos, usuario y password de
la configuración centralizada):

```bash
docker run -d --name pg-instructor -e POSTGRES_DB=instructordb -e POSTGRES_PASSWORD=postgres -p 5433:5432 postgres:16
docker run -d --name pg-alumno     -e POSTGRES_DB=alumnodb     -e POSTGRES_PASSWORD=postgres -p 5434:5432 postgres:16
docker run -d --name pg-taller     -e POSTGRES_DB=tallerdb     -e POSTGRES_PASSWORD=postgres -p 5435:5432 postgres:16
```

(Las tablas se crean solas: `ddl-auto: update`.)

## 2) Orden de arranque (en IntelliJ, Run de cada Application)

1. `ms-istana-config-server`  → http://localhost:8888
2. `ms-istana-registry-server` → http://localhost:8761 (Eureka dashboard)
3. `ms-istana-gestion-instructor`   → :8081
4. `ms-istana-gestion-alumno`       → :8082
5. `ms-istana-gestion-taller`       → :8083

Verifica en el dashboard de Eureka (`:8761`) que aparezcan los 3 servicios `UP`.

## 3) Swagger (documentación OpenAPI) de cada servicio

- http://localhost:8081/swagger-ui.html  (instructores)
- http://localhost:8082/swagger-ui.html  (alumnos)
- http://localhost:8083/swagger-ui.html  (talleres)

## 4) Pruebas en Postman

> En la Fase 3 se prueba **directo a cada servicio** (8081/8082/8083).
> En la Fase 4 estas mismas rutas pasarán por el API Gateway (:8080).

### Instructores — POST http://localhost:8081/api/instructores
```json
{
  "dni": "70123456",
  "nombres": "Benjamin",
  "apellidos": "Reyna Barreto",
  "especialidad": "Aplicaciones Distribuidas",
  "email": "benjamin.reyna@upeu.edu.pe"
}
```
- GET    http://localhost:8081/api/instructores
- GET    http://localhost:8081/api/instructores/1
- PUT    http://localhost:8081/api/instructores/1  (mismo body)
- DELETE http://localhost:8081/api/instructores/1

### Alumnos — POST http://localhost:8082/api/alumnos
```json
{
  "codigo": "202410001",
  "nombres": "Fernando",
  "apellidos": "Esteban",
  "email": "fernando.esteban@upeu.edu.pe",
  "ciclo": 5
}
```

### Talleres — POST http://localhost:8083/api/talleres
```json
{
  "codigo": "TALL-001",
  "nombre": "Microservicios con Spring Cloud",
  "descripcion": "Taller practico de arquitectura distribuida",
  "cupoMaximo": 30,
  "instructorId": 1
}
```

## 5) Pruebas de validación y errores (para evidencia técnica)

- POST instructor con `dni: "123"` → **400** con `validationErrors` (DNI debe tener 8 dígitos).
- POST instructor con email repetido → **409 CONFLICT**.
- GET http://localhost:8081/api/instructores/999 → **404 NOT_FOUND** con cuerpo `ApiError`.

## 6) Checklist de aceptación Fase 3

- [ ] Los 3 servicios arrancan y se registran en Eureka.
- [ ] CRUD completo funciona en los 3 (Postman).
- [ ] Validaciones devuelven 400 con detalle de campos.
- [ ] Duplicados devuelven 409 y no encontrado devuelve 404.
- [ ] Swagger UI carga en cada servicio.

Cuando confirmes que esto funciona, pasamos a la **Fase 4 — API Gateway**.
