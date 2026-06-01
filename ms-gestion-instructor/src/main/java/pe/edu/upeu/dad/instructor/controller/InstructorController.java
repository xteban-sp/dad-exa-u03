package pe.edu.upeu.dad.instructor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.dad.instructor.dto.InstructorRequest;
import pe.edu.upeu.dad.instructor.dto.InstructorResponse;
import pe.edu.upeu.dad.instructor.service.InstructorService;

import java.util.List;

@Tag(name = "Instructores", description = "Gestion de instructores")
@RestController
@RequestMapping("/api/instructores")
public class InstructorController {

    private final InstructorService service;

    public InstructorController(InstructorService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los instructores")
    @GetMapping
    public ResponseEntity<List<InstructorResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Obtener un instructor por id")
    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear un instructor")
    @PostMapping
    public ResponseEntity<InstructorResponse> crear(@Valid @RequestBody InstructorRequest request) {
        return new ResponseEntity<>(service.crear(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un instructor")
    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponse> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody InstructorRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un instructor")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
