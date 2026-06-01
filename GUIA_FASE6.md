# Guía de verificación — Fase 6 (Resiliencia: Circuit Breaker con Resilience4j)

## ¿Qué se logró?
Las llamadas Feign del taller ahora están protegidas por un **Circuit Breaker**.
Si un microservicio dependiente (instructor o alumno) falla o se cae:
- En vez de propagar el error, se ejecuta un **fallback** (respuesta degradada).
- Tras un porcentaje de fallos, el circuito se **ABRE** y deja de golpear al servicio caído
  (deja de esperar timeouts → respuestas rápidas).
- Pasado un tiempo, pasa a **HALF_OPEN** y se recupera solo si el servicio vuelve.

Parámetros configurados (en `ms-istana-gestion-taller-dev.yml`):
ventana de 6 llamadas, mínimo 4, umbral de fallo 50%, 10s abierto, auto half-open.
Los 404 reales **no** cuentan como fallo (solo caídas/timeouts).

## 1) Preparación
- **Reload Maven** en el taller (dependencia nueva `resilience4j`).
- **Reinicia el Config Server** (cambió la config del taller).
  Verifica: `http://localhost:8888/ms-istana-gestion-taller/dev` debe incluir el bloque `resilience4j`.
- Arranca todo en orden (config → eureka → instructor → alumno → taller → gateway).

## 2) Estado base (todo arriba)
1. Crea instructor, alumno y taller.
2. `POST /api/talleres/1/asignar-instructor/1`
3. `POST /api/talleres/1/inscribir-alumno/1`
4. `GET  /api/talleres/1/detalle-completo` → muestra instructor + alumnos. ✔

## 3) Demostración del Circuit Breaker (lo importante)

**Paso A — provoca la falla:**
Detén el microservicio **ms-istana-gestion-instructor** (Stop en IntelliJ).

**Paso B — llama varias veces (4-6):**
`GET http://localhost:8080/api/talleres/1/detalle-completo`

Observa:
- La respuesta **sigue llegando** (no se cae), pero ahora con `"instructor": null`.
  → Eso es la **tolerancia a fallos**: el taller degrada en vez de fallar.
- En la consola del taller aparece: `[CircuitBreaker] Fallback de instructor id=1 | causa=...`

**Paso C — observa el circuito abrirse:**
```
GET http://localhost:8083/actuator/circuitbreakers
```
Tras superar el umbral (≥50% de 4+ llamadas), el estado pasa a **OPEN**.
Con el circuito abierto, las llamadas responden **al instante** (ya no esperan el timeout).

También puedes ver el indicador en:
```
GET http://localhost:8083/actuator/health
```
(busca `circuitBreakers` → `ms-istana-gestion-instructor`).

**Paso D — recuperación automática:**
Vuelve a arrancar **ms-istana-gestion-instructor**. Espera ~10s y llama otra vez a
`detalle-completo`. El circuito pasa a **HALF_OPEN** y, si responde bien, vuelve a
**CLOSED**; el detalle muestra de nuevo el instructor real. ✔

## 4) Nota sobre los endpoints de escritura
Con el Circuit Breaker activo, si el servicio dependiente está caído, `asignar-instructor`
e `inscribir-alumno` reciben `null` del fallback y responden **404** (no pueden validar al
recurso). El escenario que mejor demuestra la resiliencia es `detalle-completo`, porque ahí
la degradación es visible y el sistema sigue operativo.

## 5) Checklist de aceptación Fase 6
- [ ] `detalle-completo` responde aunque instructor/alumno estén caídos (con `null`).
- [ ] Aparecen los logs de `[CircuitBreaker] Fallback ...`.
- [ ] `/actuator/circuitbreakers` muestra el estado pasar a `OPEN` tras los fallos.
- [ ] Al reactivar el servicio, el circuito vuelve a `CLOSED` (recuperación).

Siguiente: **Fase 7 — Balanceo de carga** (varias instancias de un microservicio y
reparto round-robin vía Eureka + Spring Cloud LoadBalancer).
