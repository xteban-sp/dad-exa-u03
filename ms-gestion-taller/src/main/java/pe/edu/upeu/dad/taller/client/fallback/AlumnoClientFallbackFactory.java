package pe.edu.upeu.dad.taller.client.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import pe.edu.upeu.dad.taller.client.AlumnoClient;

// Se ejecuta cuando la llamada al ms de alumnos falla
// o cuando el Circuit Breaker esta ABIERTO (tolerancia a fallos).
@Component
public class AlumnoClientFallbackFactory implements FallbackFactory<AlumnoClient> {

    private static final Logger log = LoggerFactory.getLogger(AlumnoClientFallbackFactory.class);

    @Override
    public AlumnoClient create(Throwable cause) {
        return id -> {
            log.warn("[CircuitBreaker] Fallback de alumno id={} | causa={}", id, cause.toString());
            // Respuesta degradada: null => el detalle se construye sin este alumno.
            return null;
        };
    }
}
