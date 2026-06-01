package pe.edu.upeu.dad.taller.service;

import pe.edu.upeu.dad.taller.dto.TallerDetalleResponse;
import pe.edu.upeu.dad.taller.dto.TallerRequest;
import pe.edu.upeu.dad.taller.dto.TallerResponse;

import java.util.List;

public interface TallerService {
    List<TallerResponse> listar();
    TallerResponse obtenerPorId(Long id);
    TallerResponse crear(TallerRequest request);
    TallerResponse actualizar(Long id, TallerRequest request);
    void eliminar(Long id);

    // Endpoints compuestos (comunicacion entre microservicios via Feign)
    TallerResponse asignarInstructor(Long idTaller, Long idInstructor);
    TallerDetalleResponse inscribirAlumno(Long idTaller, Long idAlumno);
    TallerDetalleResponse obtenerDetalleCompleto(Long idTaller);

    // Saga de matricula (consistencia distribuida con compensacion)
    TallerDetalleResponse matricularAlumno(Long idTaller, Long idAlumno);
}
