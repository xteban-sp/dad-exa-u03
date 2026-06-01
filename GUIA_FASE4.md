# Guía de verificación — Fase 4 (API Gateway)

## ¿Qué se logró en esta fase?
El cliente ya no le pega a cada microservicio por su puerto. Todo entra por el
**API Gateway en el puerto 8080**, que enruta a cada servicio resolviéndolo por su
nombre en Eureka (`lb://`), con CORS centralizado.

```
Cliente → :8080 (Gateway) → lb://ms-istana-gestion-instructor (:8081)
                          → lb://ms-istana-gestion-alumno     (:8082)
                          → lb://ms-istana-gestion-taller      (:8083)
```

## 1) Importar el módulo en IntelliJ
El gateway es un módulo Maven nuevo. En IntelliJ:
- `File > New > Module from Existing Sources...` y selecciona
  `ms-istana-api-gateway/pom.xml`, **o** click derecho sobre ese `pom.xml` → *Add as Maven Project*.
- Espera a que descargue dependencias.

## 2) Reinicia el Config Server
Cambió `ms-istana-api-gateway-dev.yml` (ahora trae rutas + CORS). Como está en el
classpath del Config Server, **reinicia el Config Server** para que lo recargue.
Verifica: `http://localhost:8888/ms-istana-api-gateway/dev` → debe mostrar las `routes`.

## 3) Orden de arranque
1. ms-istana-config-server (8888)
2. ms-istana-registry-server (8761)
3. ms-istana-gestion-instructor / alumno / taller (8081/8082/8083)
4. **ms-istana-api-gateway (8080)**  ← arráncalo al final

En el dashboard de Eureka (`:8761`) deben verse los 4 servicios `UP`
(instructor, alumno, taller y el gateway).

## 4) Pruebas en Postman — ahora TODO por el puerto 8080

| Antes (directo)                         | Ahora (vía Gateway)                   |
|-----------------------------------------|----------------------------------------|
| http://localhost:8081/api/instructores  | http://localhost:8080/api/instructores |
| http://localhost:8082/api/alumnos        | http://localhost:8080/api/alumnos      |
| http://localhost:8083/api/talleres       | http://localhost:8080/api/talleres     |

Repite el CRUD de la Fase 3 pero apuntando a `:8080`. Mismos JSON, misma respuesta.
Ejemplo:

**POST** `http://localhost:8080/api/instructores`
```json
{
  "dni": "70123456",
  "nombres": "Benjamin",
  "apellidos": "Reyna Barreto",
  "especialidad": "Aplicaciones Distribuidas",
  "email": "benjamin.reyna@upeu.edu.pe"
}
```

## 5) Verificar las rutas del Gateway (evidencia técnica)
Con el actuator de gateway expuesto:
```
GET http://localhost:8080/actuator/gateway/routes
```
Debe listar las 3 rutas (`ms-istana-gestion-instructor`, `ms-istana-gestion-alumno`, `ms-istana-gestion-taller`).

## 6) Checklist de aceptación Fase 4
- [ ] El Gateway aparece `UP` en Eureka.
- [ ] `GET/POST/PUT/DELETE` de los 3 dominios funcionan vía `:8080`.
- [ ] `/actuator/gateway/routes` muestra las 3 rutas.
- [ ] Una petición con `Origin` distinto recibe cabeceras CORS (`Access-Control-Allow-Origin`).

## Posible error y solución
Si el Gateway falla al arrancar con:
`Spring MVC found on classpath, which is incompatible with Spring Cloud Gateway`
→ es porque entró Tomcat. Ya lo prevenimos excluyendo `spring-boot-starter-web` en el
`pom` del gateway; si reaparece, haz *Reload* del proyecto Maven en IntelliJ.

Cuando confirmes el ruteo por 8080, seguimos con la **Fase 5 — Comunicación entre
microservicios (OpenFeign)**.
