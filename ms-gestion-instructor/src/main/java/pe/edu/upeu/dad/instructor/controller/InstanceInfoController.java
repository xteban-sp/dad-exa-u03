package pe.edu.upeu.dad.instructor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

// Endpoint de apoyo para DEMOSTRAR el balanceo de carga:
// devuelve el puerto/instancia que atendio la peticion.
@Tag(name = "Instancia", description = "Identifica que instancia responde (demo de balanceo)")
@RestController
@RequestMapping("/api/instructores")
public class InstanceInfoController {

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name}")
    private String appName;

    @Operation(summary = "Indica que instancia (puerto) atendio la peticion")
    @GetMapping("/whoami")
    public Map<String, String> whoami() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("servicio", appName);
        info.put("puerto", port);
        info.put("instancia", appName + ":" + port);
        return info;
    }
}
