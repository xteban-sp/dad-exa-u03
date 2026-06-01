# Guía de verificación — Fase 7 (Balanceo de carga)

## ¿Qué se logró?
Spring Cloud LoadBalancer (que ya viene con Eureka) reparte las peticiones entre
**varias instancias** del mismo microservicio usando **round-robin**, tanto desde el
Gateway (`lb://`) como desde Feign (en el taller). No hizo falta código de balanceo:
solo levantar 2 instancias y observar el reparto.

Se añadió un endpoint de apoyo: `GET /api/instructores/whoami` que devuelve el puerto
de la instancia que respondió, para *ver* el balanceo.

## 1) Preparar la 2ª instancia en IntelliJ
El puerto del instructor (8081) viene del Config Server. Para una 2ª instancia en 8091,
**lo sobreescribimos por línea de comando** (tiene mayor prioridad que el Config Server).

1. Menú **Run → Edit Configurations...**
2. Selecciona la configuración de `MsIstanaGestionInstructorApplication` y pulsa **Copy Configuration** (icono de copiar).
3. Renómbrala a `MsIstanaGestionInstructorApplication (8091)`.
4. En **Program arguments** escribe:
   ```
   --server.port=8091
   ```
   (Si no ves "Program arguments", actívalo en *Modify options → Program arguments*.)
5. Aceptar.

> El `instance-id` ya usa `${server.port}`, así que la 2ª instancia se registra como
> `localhost:ms-istana-gestion-instructor:8091`, distinta de la de 8081.

## 2) Arrancar
1. Config Server (8888) → Eureka (8761).
2. Arranca la instancia normal de instructor (8081).
3. Arranca la instancia copiada (8091).
4. Arranca alumno, taller y gateway.

En `http://localhost:8761`, bajo **MS-ISTANA-GESTION-INSTRUCTOR** deben aparecer **2 instancias**
(`...:8081` y `...:8091`).

## 3) Ver el balanceo (round-robin)

**Vía Gateway** — llama varias veces (carpeta "Fase 7" en Postman):
```
GET http://localhost:8080/api/instructores/whoami
```
La respuesta debe **alternar** el puerto en cada llamada:
```json
{ "servicio": "ms-istana-gestion-instructor", "puerto": "8081", "instancia": "ms-istana-gestion-instructor:8081" }
```
luego `8091`, luego `8081`, ... → eso es el round-robin.

**Vía Feign (desde el taller):** llama varias veces a
`GET http://localhost:8080/api/talleres/1/detalle-completo`
y observa en las consolas de las DOS instancias de instructor cómo las peticiones
(`show-sql` / logs de acceso) caen alternadamente en una y otra. El taller balancea igual
que el Gateway porque ambos usan el mismo LoadBalancer.

## 4) Tolerancia (combina con Fase 6)
Apaga la instancia 8091: el LoadBalancer deja de mandarle tráfico y todo sigue por la 8081.
Si apagas ambas, entra el Circuit Breaker (fallback). Resiliencia + balanceo juntos.

## 5) Checklist de aceptación Fase 7
- [ ] Las 2 instancias de instructor aparecen en Eureka.
- [ ] `whoami` vía Gateway alterna entre 8081 y 8091.
- [ ] Las llamadas Feign del taller se reparten entre ambas instancias.
- [ ] Al apagar una instancia, el tráfico sigue por la otra sin error.

Siguiente: **Fase 8 — Seguridad JWT** (autenticación con token; aquí entra el auth y los
roles que mencionaste).
