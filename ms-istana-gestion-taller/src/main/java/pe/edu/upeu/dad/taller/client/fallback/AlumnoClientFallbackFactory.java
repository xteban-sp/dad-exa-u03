package pe.edu.upeu.dad.taller.client.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import pe.edu.upeu.dad.taller.client.AlumnoClient;
import pe.edu.upeu.dad.taller.client.dto.AlumnoDto;

// Se ejecuta cuando la llamada al ms de alumnos falla
// o cuando el Circuit Breaker esta ABIERTO (tolerancia a fallos).
@Component
public class AlumnoClientFallbackFactory implements FallbackFactory<AlumnoClient> {

    private static final Logger log = LoggerFactory.getLogger(AlumnoClientFallbackFactory.class);

    @Override
    public AlumnoClient create(Throwable cause) {
        return new AlumnoClient() {
            @Override
            public AlumnoDto obtenerPorId(Long id) {
                // Lectura: degrada a null (el detalle se construye sin este alumno)
                log.warn("[CircuitBreaker] Fallback de alumno id={} | causa={}", id, cause.toString());
                return null;
            }

            @Override
            public AlumnoDto incrementarTaller(Long id) {
                // Escritura (paso de Saga): NO se puede degradar en silencio.
                // Propaga el fallo para que la Saga aborte/compense.
                log.warn("[CircuitBreaker] Fallback incrementarTaller alumno id={} | causa={}", id, cause.toString());
                throw new IllegalStateException("ms-istana-gestion-alumno no disponible (incrementar)");
            }

            @Override
            public AlumnoDto decrementarTaller(Long id) {
                log.warn("[CircuitBreaker] Fallback decrementarTaller alumno id={} | causa={}", id, cause.toString());
                throw new IllegalStateException("ms-istana-gestion-alumno no disponible (decrementar)");
            }
        };
    }
}
