package pe.edu.upeu.dad.instructor.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.dad.instructor.dto.InstructorRequest;
import pe.edu.upeu.dad.instructor.dto.InstructorResponse;
import pe.edu.upeu.dad.instructor.entity.Instructor;

@Component
public class InstructorMapper {

    public Instructor toEntity(InstructorRequest dto) {
        Instructor e = new Instructor();
        e.setDni(dto.getDni());
        e.setNombres(dto.getNombres());
        e.setApellidos(dto.getApellidos());
        e.setEspecialidad(dto.getEspecialidad());
        e.setEmail(dto.getEmail());
        e.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        return e;
    }

    public void updateEntity(Instructor e, InstructorRequest dto) {
        e.setDni(dto.getDni());
        e.setNombres(dto.getNombres());
        e.setApellidos(dto.getApellidos());
        e.setEspecialidad(dto.getEspecialidad());
        e.setEmail(dto.getEmail());
        if (dto.getEstado() != null) e.setEstado(dto.getEstado());
    }

    public InstructorResponse toResponse(Instructor e) {
        InstructorResponse r = new InstructorResponse();
        r.setId(e.getId());
        r.setDni(e.getDni());
        r.setNombres(e.getNombres());
        r.setApellidos(e.getApellidos());
        r.setEspecialidad(e.getEspecialidad());
        r.setEmail(e.getEmail());
        r.setEstado(e.getEstado());
        return r;
    }
}
