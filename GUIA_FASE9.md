# Guía de verificación — Fase 9 (Políticas y filtros de seguridad)

## ¿Qué se logró?
El **API Gateway** ahora es el **guardián centralizado**: un filtro global
(`AuthenticationGlobalFilter`) intercepta TODA petición, valida el JWT (con el mismo
secreto que `ms-auth`) y aplica **autorización por rol**. Además inyecta cabeceras
`X-Auth-User` y `X-Auth-Rol` hacia los microservicios.

### Política aplicada
| Operación | Roles permitidos |
|-----------|------------------|
| `/api/auth/**` (login/register) | Público (sin token) |
| `GET` (lecturas) en cualquier recurso | ADMIN, INSTRUCTOR, ALUMNO |
| `POST/PUT/DELETE` instructores, alumnos, talleres | Solo ADMIN |
| `POST /api/talleres/{id}/inscribir-alumno/{id}` | ALUMNO (y ADMIN) |
| Sin token / token inválido | 401 |
| Rol sin permiso | 403 |

## 1) Preparación
- **Rebuild Project** y reinicia el **Config Server** (cambió la config del gateway: ahora
  trae `jwt.secret`). Verifica en `http://localhost:8888/ms-admin-api-gateway/dev` que
  aparezca `jwt.secret` **idéntico** al de `ms-auth`.
- **Reload Maven** en el gateway (dependencia jjwt nueva) y reinícialo.
- Ten arriba: Config, Eureka, ms-auth, instructor, alumno, taller, gateway.

> Importante: el `jwt.secret` del gateway y el de ms-auth deben ser EXACTAMENTE iguales,
> o la validación fallará (401 en todo).

## 2) Pruebas (carpeta "Fase 9" en Postman)

Ejecuta en este orden:

1. **Login (admin)** (carpeta Fase 8) → guarda `{{jwt}}`.
2. **Login alumno1** (Fase 9) → guarda `{{jwtAlumno}}`.
3. **GET instructores SIN token** → **401** (falta token).
4. **GET instructores con token** (alumno) → **200** (lectura permitida a todos).
5. **POST instructor como ALUMNO** → **403** (escritura no permitida a ALUMNO).
6. **POST instructor como ADMIN** → **201** (ADMIN sí puede).
7. **Inscribir alumno como ALUMNO** → **201** (excepción de la política: el alumno puede inscribirse).

## 3) Qué observar
- El **401** y **403** los devuelve el **Gateway** (no llega siquiera al microservicio).
- En una petición autorizada, el microservicio recibe las cabeceras `X-Auth-User` y
  `X-Auth-Rol` (útil para auditoría o lógica futura).

## 4) Checklist de aceptación Fase 9
- [ ] Sin token, cualquier ruta protegida responde 401.
- [ ] Token válido permite las lecturas a cualquier rol.
- [ ] Escrituras solo las permite ADMIN (ALUMNO/INSTRUCTOR → 403).
- [ ] El ALUMNO puede inscribirse en un taller.
- [ ] `/api/auth/**` sigue siendo público (login funciona sin token).

## Nota de arquitectura (para la defensa)
La seguridad está **centralizada en el Gateway** (patrón "edge security"): un solo punto
valida tokens y aplica políticas, y los microservicios quedan simples. Una alternativa más
estricta sería que cada microservicio validara también el token (defensa en profundidad);
se menciona como mejora, pero para este proyecto el Gateway como guardián es suficiente y
es el patrón que muestra tu diagrama (JWT en el Gateway).

Con esto cierras **Seguridad (U2-S3 y S4)**. Siguiente: **Fase 10 — Consistencia distribuida
(patrón Saga)** para operaciones multi-servicio.
