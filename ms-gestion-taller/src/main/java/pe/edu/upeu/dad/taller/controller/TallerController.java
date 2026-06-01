package pe.edu.upeu.dad.taller.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.dad.taller.dto.TallerDetalleResponse;
import pe.edu.upeu.dad.taller.dto.TallerRequest;
import pe.edu.upeu.dad.taller.dto.TallerResponse;
import pe.edu.upeu.dad.taller.service.TallerService;

import java.util.List;

@Tag(name = "Talleres", description = "Gestion de talleres y operaciones compuestas")
@RestController
@RequestMapping("/api/talleres")
public class TallerController {

    private final TallerService service;

    public TallerController(TallerService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los talleres")
    @GetMapping
    public ResponseEntity<List<TallerResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Obtener un taller por id")
    @GetMapping("/{id}")
    public ResponseEntity<TallerResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear un taller")
    @PostMapping
    public ResponseEntity<TallerResponse> crear(@Valid @RequestBody TallerRequest request) {
        return new ResponseEntity<>(service.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un taller")
    @PutMapping("/{id}")
    public ResponseEntity<TallerResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody TallerRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un taller")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Endpoints compuestos (Feign) ----------

    @Operation(summary = "Asignar un instructor a un taller (valida via microservicio de instructores)")
    @PostMapping("/{idTaller}/asignar-instructor/{idInstructor}")
    public ResponseEntity<TallerResponse> asignarInstructor(@PathVariable Long idTaller,
                                                            @PathVariable Long idInstructor) {
        return ResponseEntity.ok(service.asignarInstructor(idTaller, idInstructor));
    }

    @Operation(summary = "Inscribir un alumno en un taller (valida cupo y via microservicio de alumnos)")
    @PostMapping("/{idTaller}/inscribir-alumno/{idAlumno}")
    public ResponseEntity<TallerDetalleResponse> inscribirAlumno(@PathVariable Long idTaller,
                                                                 @PathVariable Long idAlumno) {
        return new ResponseEntity<>(service.inscribirAlumno(idTaller, idAlumno), HttpStatus.CREATED);
    }

    @Operation(summary = "Detalle completo del taller (instructor + alumnos inscritos via Feign)")
    @GetMapping("/{idTaller}/detalle-completo")
    public ResponseEntity<TallerDetalleResponse> detalleCompleto(@PathVariable Long idTaller) {
        return ResponseEntity.ok(service.obtenerDetalleCompleto(idTaller));
    }
}
