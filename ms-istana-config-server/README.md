# ms-istana-config-server (FASE 1)

Servidor de configuracion centralizada para la arquitectura de microservicios.

## Requisitos

- Java 21
- Maven 3.9+

## Ejecutar

Desde esta carpeta:

```bash
mvn spring-boot:run
```

El servicio levanta en `http://localhost:8888`.

## Verificar

- Health:
  - `http://localhost:8888/actuator/health`

- Configuracion de ejemplo por servicio/perfil:
  - `http://localhost:8888/ms-istana-api-gateway/dev`
  - `http://localhost:8888/ms-istana-registry-server/dev`
  - `http://localhost:8888/ms-istana-gestion-instructor/dev`
  - `http://localhost:8888/ms-istana-gestion-alumno/dev`
  - `http://localhost:8888/ms-istana-gestion-taller/dev`

## Nota para siguientes fases

Los microservicios cliente deben incluir `spring-cloud-starter-config` y apuntar a:

- `spring.config.import=configserver:http://localhost:8888`

Tambien deben arrancar con perfil `dev` para consumir estos archivos.
