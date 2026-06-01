package pe.edu.upeu.dad.taller.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upeu.dad.taller.client.dto.AlumnoDto;
import pe.edu.upeu.dad.taller.client.fallback.AlumnoClientFallbackFactory;

// Resuelve "ms-gestion-alumno" por Eureka (descubrimiento + balanceo).
// fallbackFactory: si el servicio falla o el circuito esta abierto, se usa la respuesta degradada.
@FeignClient(name = "ms-gestion-alumno", path = "/api/alumnos",
        fallbackFactory = AlumnoClientFallbackFactory.class)
public interface AlumnoClient {

    @GetMapping("/{id}")
    AlumnoDto obtenerPorId(@PathVariable("id") Long id);
}
