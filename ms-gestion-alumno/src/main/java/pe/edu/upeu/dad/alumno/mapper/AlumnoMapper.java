package pe.edu.upeu.dad.alumno.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.dad.alumno.dto.AlumnoRequest;
import pe.edu.upeu.dad.alumno.dto.AlumnoResponse;
import pe.edu.upeu.dad.alumno.entity.Alumno;

@Component
public class AlumnoMapper {

    public Alumno toEntity(AlumnoRequest dto) {
        Alumno e = new Alumno();
        e.setCodigo(dto.getCodigo());
        e.setNombres(dto.getNombres());
        e.setApellidos(dto.getApellidos());
        e.setEmail(dto.getEmail());
        e.setCiclo(dto.getCiclo());
        e.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        return e;
    }

    public void updateEntity(Alumno e, AlumnoRequest dto) {
        e.setCodigo(dto.getCodigo());
        e.setNombres(dto.getNombres());
        e.setApellidos(dto.getApellidos());
        e.setEmail(dto.getEmail());
        e.setCiclo(dto.getCiclo());
        if (dto.getEstado() != null) e.setEstado(dto.getEstado());
    }

    public AlumnoResponse toResponse(Alumno e) {
        AlumnoResponse r = new AlumnoResponse();
        r.setId(e.getId());
        r.setCodigo(e.getCodigo());
        r.setNombres(e.getNombres());
        r.setApellidos(e.getApellidos());
        r.setEmail(e.getEmail());
        r.setCiclo(e.getCiclo());
        r.setEstado(e.getEstado());
        r.setTalleresInscritos(e.getTalleresInscritos());
        return r;
    }
}
