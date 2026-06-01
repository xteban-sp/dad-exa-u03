# Diagrama de despliegue

Despliegue containerizado con Docker Compose. Todos los contenedores comparten la red
`microservices-net`. El cliente accede únicamente por el **API Gateway (8080)**.

```mermaid
graph TD
    Cliente["Cliente / Postman"] -->|HTTP :8080| GW

    subgraph host["Host (Docker Compose - red microservices-net)"]
        CFG["config-server<br/>:8888"]
        REG["registry-server (Eureka)<br/>:8761"]
        GW["api-gateway<br/>:8080 (JWT + CORS)"]
        AUTH["ms-istana-auth<br/>:8084"]
        INS["ms-istana-gestion-instructor<br/>:8081"]
        ALU["ms-istana-gestion-alumno<br/>:8082"]
        TAL["ms-istana-gestion-taller<br/>:8083"]

        PGI[("pg-instructor<br/>:5433")]
        PGA[("pg-alumno<br/>:5434")]
        PGT[("pg-taller<br/>:5435")]
        PGU[("pg-auth<br/>:5436")]
    end

    GW -->|enruta lb://| AUTH
    GW -->|enruta lb://| INS
    GW -->|enruta lb://| ALU
    GW -->|enruta lb://| TAL

    AUTH -.->|config| CFG
    INS -.->|config| CFG
    ALU -.->|config| CFG
    TAL -.->|config| CFG
    GW  -.->|config| CFG
    REG -.->|config| CFG

    AUTH -.->|registro| REG
    INS -.->|registro| REG
    ALU -.->|registro| REG
    TAL -.->|registro| REG
    GW  -.->|registro| REG

    TAL -->|Feign| INS
    TAL -->|Feign + Saga| ALU

    AUTH --> PGU
    INS --> PGI
    ALU --> PGA
    TAL --> PGT
```

## Leyenda
- **Flechas continuas**: tráfico de peticiones (HTTP / Feign).
- **Flechas punteadas**: configuración (Config Server) y registro/descubrimiento (Eureka).
- **Cilindros**: bases de datos PostgreSQL (una por microservicio).

## Notas de despliegue
- Solo el **API Gateway (8080)** se expone al exterior; es el único punto de entrada y aplica
  la seguridad (validación JWT + autorización por rol) y CORS.
- Cada microservicio de negocio tiene su **propia base de datos** (patrón *database per service*),
  lo que mantiene el desacoplamiento.
- El **Config Server** debe iniciar primero; el resto reintenta (`restart: on-failure`) hasta
  que esté disponible.
- Dentro de la red de Docker los servicios se resuelven por **nombre de contenedor**
  (override por variables de entorno), no por `localhost`.
- Escalado/balanceo: se pueden levantar múltiples instancias de un microservicio
  (`docker compose up --scale ms-istana-gestion-instructor=2`) y el Gateway/Feign reparten con
  Spring Cloud LoadBalancer.

## Orden de arranque
```
pg-* (bases)  ->  config-server  ->  registry-server  ->  ms-istana-auth / instructor / alumno / taller  ->  api-gateway
```
