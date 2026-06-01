package pe.edu.upeu.dad.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "dad-upeu-microservicios-jwt-secret-key-2026-super-segura-0123456789";

    @Test
    void generarYValidar_devuelveSubjectYRol() {
        JwtService jwt = new JwtService(SECRET, 3600000L);
        String token = jwt.generarToken("admin", "ADMIN");

        Claims claims = jwt.validarYObtenerClaims(token);
        assertEquals("admin", claims.getSubject());
        assertEquals("ADMIN", claims.get("rol"));
    }

    @Test
    void tokenManipulado_lanzaJwtException() {
        JwtService jwt = new JwtService(SECRET, 3600000L);
        String token = jwt.generarToken("admin", "ADMIN") + "xxx";
        assertThrows(JwtException.class, () -> jwt.validarYObtenerClaims(token));
    }
}
