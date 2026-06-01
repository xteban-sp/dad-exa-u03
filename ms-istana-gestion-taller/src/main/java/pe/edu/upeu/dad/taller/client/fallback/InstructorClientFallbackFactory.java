package pe.edu.upeu.dad.taller.client.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import pe.edu.upeu.dad.taller.client.InstructorClient;

// Se ejecuta cuando la llamada al ms de instructores falla
// o cuando el Circuit Breaker esta ABIERTO (tolerancia a fallos).
@Component
public class InstructorClientFallbackFactory implements FallbackFactory<InstructorClient> {

    private static final Logger log = LoggerFactory.getLogger(InstructorClientFallbackFactory.class);

    @Override
    public InstructorClient create(Throwable cause) {
        return id -> {
            log.warn("[CircuitBreaker] Fallback de instructor id={} | causa={}", id, cause.toString());
            // Respuesta degradada: null => el detalle se construye sin instructor.
            return null;
        };
    }
}
