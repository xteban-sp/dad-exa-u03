package pe.edu.upeu.dad.taller.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.edu.upeu.dad.taller.client.dto.InstructorDto;
import pe.edu.upeu.dad.taller.client.fallback.InstructorClientFallbackFactory;

// Resuelve "ms-istana-gestion-instructor" por Eureka (descubrimiento + balanceo).
// fallbackFactory: si el servicio falla o el circuito esta abierto, se usa la respuesta degradada.
@FeignClient(name = "ms-istana-gestion-instructor", path = "/api/instructores",
        fallbackFactory = InstructorClientFallbackFactory.class)
public interface InstructorClient {

    @GetMapping("/{id}")
    InstructorDto obtenerPorId(@PathVariable("id") Long id);
}
