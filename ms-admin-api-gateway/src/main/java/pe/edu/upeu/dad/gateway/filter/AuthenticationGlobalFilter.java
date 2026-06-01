package pe.edu.upeu.dad.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pe.edu.upeu.dad.gateway.security.JwtValidator;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

// Filtro global: autentica (valida JWT) y autoriza (por rol) todas las peticiones,
// salvo las rutas publicas. Es el "guardian" centralizado de la arquitectura.
@Component
public class AuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private final JwtValidator jwtValidator;

    // Rutas que NO requieren token
    private static final List<String> RUTAS_PUBLICAS = List.of(
            "/api/auth/"
    );

    public AuthenticationGlobalFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // Preflight CORS: pasa sin token
        if (method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Rutas publicas: pasan sin token
        if (esPublica(path)) {
            return chain.filter(exchange);
        }

        // 1) Autenticacion: debe traer un Bearer token valido
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return error(exchange, HttpStatus.UNAUTHORIZED, "Falta el token (Authorization: Bearer)");
        }
        String token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtValidator.validarYObtenerClaims(token);
        } catch (JwtException e) {
            return error(exchange, HttpStatus.UNAUTHORIZED, "Token JWT invalido o expirado");
        }

        String username = claims.getSubject();
        String rol = String.valueOf(claims.get("rol"));

        // 2) Autorizacion: politica por rol
        if (!autorizado(method, path, rol)) {
            return error(exchange, HttpStatus.FORBIDDEN,
                    "El rol " + rol + " no tiene permiso para " + method + " " + path);
        }

        // 3) Propaga la identidad a los microservicios aguas abajo
        ServerHttpRequest mutado = request.mutate()
                .header("X-Auth-User", username)
                .header("X-Auth-Rol", rol)
                .build();
        return chain.filter(exchange.mutate().request(mutado).build());
    }

    private boolean esPublica(String path) {
        return RUTAS_PUBLICAS.stream().anyMatch(path::startsWith);
    }

    // Politica de autorizacion por rol
    private boolean autorizado(HttpMethod method, String path, String rol) {
        // ADMIN puede todo
        if ("ADMIN".equals(rol)) {
            return true;
        }
        // Lectura (GET): cualquier usuario autenticado
        if (method == HttpMethod.GET) {
            return true;
        }
        // Inscribir / matricular alumno en taller: permitido a ALUMNO
        if (method == HttpMethod.POST
                && (path.matches("/api/talleres/\\d+/inscribir-alumno/\\d+")
                || path.matches("/api/talleres/\\d+/matricular-alumno/\\d+"))) {
            return "ALUMNO".equals(rol);
        }
        // Cualquier otra escritura: solo ADMIN (ya retorno true arriba)
        return false;
    }

    private Mono<Void> error(ServerWebExchange exchange, HttpStatus status, String mensaje) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), mensaje);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // Alta prioridad: se ejecuta antes de enrutar
        return -1;
    }
}
