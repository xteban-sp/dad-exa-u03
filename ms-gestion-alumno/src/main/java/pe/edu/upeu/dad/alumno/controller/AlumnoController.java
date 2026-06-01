package pe.edu.upeu.dad.alumno.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.dad.alumno.dto.AlumnoRequest;
import pe.edu.upeu.dad.alumno.dto.AlumnoResponse;
import pe.edu.upeu.dad.alumno.service.AlumnoService;

import java.util.List;

@Tag(name = "Alumnos", description = "Gestion de alumnos")
@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {

    private final AlumnoService service;

    public AlumnoController(AlumnoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los alumnos")
    @GetMapping
    public ResponseEntity<List<AlumnoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Obtener un alumno por id")
    @GetMapping("/{id}")
    public ResponseEntity<AlumnoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear un alumno")
    @PostMapping
    public ResponseEntity<AlumnoResponse> crear(@Valid @RequestBody AlumnoRequest request) {
        return new ResponseEntity<>(service.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un alumno")
    @PutMapping("/{id}")
    public ResponseEntity<AlumnoResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody AlumnoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un alumno")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Operaciones internas para la Saga de matricula (llamadas por ms-gestion-taller) ---

    @Operation(summary = "Incrementar el contador de talleres del alumno (paso de Saga)")
    @PostMapping("/{id}/incrementar-taller")
    public ResponseEntity<AlumnoResponse> incrementarTaller(@PathVariable Long id) {
        return ResponseEntity.ok(service.incrementarTaller(id));
    }

    @Operation(summary = "Decrementar el contador de talleres del alumno (compensacion de Saga)")
    @PostMapping("/{id}/decrementar-taller")
    public ResponseEntity<AlumnoResponse> decrementarTaller(@PathVariable Long id) {
        return ResponseEntity.ok(service.decrementarTaller(id));
    }
}
