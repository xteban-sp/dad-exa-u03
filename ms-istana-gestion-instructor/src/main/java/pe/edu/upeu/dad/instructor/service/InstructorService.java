package pe.edu.upeu.dad.instructor.service;

import pe.edu.upeu.dad.instructor.dto.InstructorRequest;
import pe.edu.upeu.dad.instructor.dto.InstructorResponse;

import java.util.List;

public interface InstructorService {
    List<InstructorResponse> listar();
    InstructorResponse obtenerPorId(Long id);
    InstructorResponse crear(InstructorRequest request);
    InstructorResponse actualizar(Long id, InstructorRequest request);
    void eliminar(Long id);
}
