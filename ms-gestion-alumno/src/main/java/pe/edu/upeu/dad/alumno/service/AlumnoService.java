package pe.edu.upeu.dad.alumno.service;

import pe.edu.upeu.dad.alumno.dto.AlumnoRequest;
import pe.edu.upeu.dad.alumno.dto.AlumnoResponse;

import java.util.List;

public interface AlumnoService {
    List<AlumnoResponse> listar();
    AlumnoResponse obtenerPorId(Long id);
    AlumnoResponse crear(AlumnoRequest request);
    AlumnoResponse actualizar(Long id, AlumnoRequest request);
    void eliminar(Long id);
}
