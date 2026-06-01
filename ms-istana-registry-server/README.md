# ms-istana-registry-server (FASE 2)

Servidor de registro y descubrimiento de servicios con Eureka.

## Requisitos

- Java 21
- Maven 3.9+
- `ms-istana-config-server` ejecutandose en `http://localhost:8888`

## Ejecutar

Desde esta carpeta:

```bash
mvn spring-boot:run
```

## Verificar

- Consola Eureka:
  - `http://localhost:8761`

- Health:
  - `http://localhost:8761/actuator/health`

## Notas

- El servicio obtiene configuracion desde Config Server usando:
  - `spring.config.import=optional:configserver:http://localhost:8888`
- Perfil activo por defecto:
  - `dev`
