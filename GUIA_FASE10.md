# Guía de verificación — Fase 10 (Consistencia distribuida: patrón Saga)

## ¿Qué se logró?
Una operación que toca **dos microservicios con BD distintas** (taller + alumno) no puede
usar una transacción ACID normal. Se implementó el **patrón Saga (orquestación)**: el taller
orquesta los pasos y, si alguno falla, ejecuta una **compensación** que deshace lo anterior.

### Operación: matricular alumno
```
POST /api/talleres/{idTaller}/matricular-alumno/{idAlumno}
```
Pasos orquestados por el taller:
1. **Paso 1 (remoto):** `alumnoClient.incrementarTaller(idAlumno)` → +1 al contador del alumno
   (regla: máximo 3 talleres). Commit en la BD de alumno.
2. **Paso 2 (local):** crear la inscripción validando cupo. Commit en la BD de taller.
3. **Si el Paso 2 falla** (sin cupo) → **Compensación:** `decrementarTaller(idAlumno)` revierte
   el Paso 1. Así el contador del alumno no queda inflado: el sistema queda **consistente**.

> Clave: el método NO es `@Transactional` global a propósito. Cada paso confirma su propia
> transacción; por eso la reversión es una **compensación explícita**, no un rollback ACID.

## 1) Preparación
- **Rebuild Project** + reinicia **Config Server**.
- **Reload Maven** en alumno, taller y gateway. Reinícialos (y ms-auth/instructor).
- El servicio alumno añadió la columna `talleresInscritos` (se crea sola con ddl-auto).

## 2) Caso A — Matrícula exitosa (carpeta "Fase 10")
1. Login admin y login alumno1 (guardan `{{jwt}}` y `{{jwtAlumno}}`).
2. Crea instructor, alumno (id 1) y un taller con cupo (id 1).
3. `POST /api/talleres/1/matricular-alumno/1` → **201**.
4. `GET /api/alumnos/1` → `talleresInscritos` = **1** (subió por el Paso 1).
5. `GET /api/talleres/1/detalle-completo` → el alumno aparece inscrito.

## 3) Caso B — Compensación (lo importante)
1. Crea un **taller con cupo=1** (request "Crear taller lleno"). Anota su id (ej. 2).
2. Matricula un alumno A en ese taller → **201** (ocupa el único cupo).
3. Intenta matricular un alumno B (id distinto) en el taller 2:
   `POST /api/talleres/2/matricular-alumno/{idB}`
   → **409** "Matricula revertida (Saga): ... no tiene cupo disponible".
4. Verifica `GET /api/alumnos/{idB}` → `talleresInscritos` **NO** aumentó (sigue igual).
   → Eso prueba la **compensación**: el Paso 1 se incrementó y luego se revirtió.

En la **consola del taller** verás la secuencia:
```
[SAGA] Inicio matricula taller=2 alumno=B
[SAGA] Paso 1 OK: contador del alumno B incrementado
[SAGA] Paso 2 FALLO (... no tiene cupo disponible). Compensando Paso 1...
[SAGA] Compensacion OK: contador del alumno B revertido
```

## 4) Caso C — Regla de máximo (aborto sin compensación)
Matricula al mismo alumno en 4 talleres distintos. El **4º** falla en el Paso 1
(`ya alcanzo el maximo de 3 talleres`) → la Saga aborta **antes** de tocar nada local,
así que no hay inscripción ni compensación que hacer. Respuesta **409**.

## 5) Checklist de aceptación Fase 10
- [ ] Matrícula exitosa: sube `talleresInscritos` y se crea la inscripción.
- [ ] Matrícula sin cupo: responde 409 y `talleresInscritos` queda revertido (compensación).
- [ ] Los logs `[SAGA] ...` muestran los pasos y la compensación.
- [ ] La regla de máximo 3 aborta limpiamente sin dejar estados a medias.

Con esto cierras la **Unidad 2 completa** (resiliencia + balanceo + seguridad + consistencia).
Siguiente: **Fase 11 — Dockerización** (Dockerfile por servicio + docker-compose con todo).
