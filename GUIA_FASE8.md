# Guía de verificación — Fase 8 (Seguridad JWT)

## ¿Qué se logró?
Se creó el microservicio **`ms-auth`** (puerto 8084) que:
- Gestiona usuarios con **roles** (`ADMIN`, `INSTRUCTOR`, `ALUMNO`).
- Guarda las contraseñas cifradas con **BCrypt**.
- **Emite un JWT** firmado (HS256) en el login.
- **Valida** tokens (endpoint `/validate`).

> Esta fase es la **autenticación** (emitir y validar token). La **autorización por rol en
> cada ruta** (que el Gateway exija el token y filtre por rol) es la **Fase 9**, tal como tu
> sílabo separa S3 (JWT) y S4 (políticas y filtros).

## 1) Base de datos del auth
```bash
docker run -d --name pg-auth -e POSTGRES_DB=authdb -e POSTGRES_PASSWORD=postgres -p 5436:5432 postgres:16
```

## 2) Importar y arrancar
- **Rebuild Project** (config nueva en el Config Server) y reinicia el **Config Server**.
  Verifica: `http://localhost:8888/ms-auth/dev` muestra `jwt.secret`, `server.port: 8084`.
- Importa el módulo `ms-auth/pom.xml` en IntelliJ (Add as Maven Project).
- Arranca en orden: Config → Eureka → ms-auth → (instructor/alumno/taller) → Gateway.
- En Eureka debe aparecer **MS-AUTH**.

Al arrancar, `ms-auth` siembra 3 usuarios de prueba:

| Usuario      | Password    | Rol        |
|--------------|-------------|------------|
| admin        | admin123    | ADMIN      |
| instructor1  | inst123     | INSTRUCTOR |
| alumno1      | alum123     | ALUMNO     |

## 3) Probar (carpeta "Fase 8" en Postman)

**Login** — `POST http://localhost:8080/api/auth/login`
```json
{ "username": "admin", "password": "admin123" }
```
Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "username": "admin",
  "rol": "ADMIN",
  "expiraEnSegundos": 3600
}
```
> El request de Postman guarda el token automáticamente en la variable `{{jwt}}`.

**Register** — `POST http://localhost:8080/api/auth/register`
```json
{ "username": "fernando", "password": "fernando123", "rol": "ALUMNO" }
```
→ 201 y devuelve también un token para el nuevo usuario.

**Validate** — `GET http://localhost:8080/api/auth/validate`
con header `Authorization: Bearer {{jwt}}`
```json
{ "valido": true, "username": "admin", "rol": "ADMIN", "expira": "..." }
```

## 4) Casos de error (evidencia técnica)
- Login con password incorrecta → **401 UNAUTHORIZED** (`Credenciales invalidas`).
- Register con un username que ya existe → **409 CONFLICT**.
- `validate` con un token manipulado o vencido → **401 INVALID_TOKEN**.

## 5) Decodifica el token (opcional, para entenderlo)
Pega el `token` en https://jwt.io y verás el payload: `sub` (username), `rol`, `iat`, `exp`.
La firma se valida con el secreto compartido (`jwt.secret`).

## 6) Checklist de aceptación Fase 8
- [ ] `ms-auth` aparece UP en Eureka y accesible vía Gateway (`/api/auth/**`).
- [ ] `login` con credenciales válidas devuelve un JWT.
- [ ] Las contraseñas se guardan cifradas (mira la tabla `usuarios`: el hash BCrypt).
- [ ] `validate` devuelve username y rol del token.
- [ ] Credenciales malas → 401; token inválido → 401; username repetido → 409.

Siguiente: **Fase 9 — Políticas y filtros** (el Gateway exige el JWT en las rutas
protegidas y autoriza por rol: ADMIN, INSTRUCTOR, ALUMNO).
