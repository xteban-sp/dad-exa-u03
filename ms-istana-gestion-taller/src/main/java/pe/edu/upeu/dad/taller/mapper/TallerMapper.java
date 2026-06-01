package pe.edu.upeu.dad.taller.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.dad.taller.dto.TallerRequest;
import pe.edu.upeu.dad.taller.dto.TallerResponse;
import pe.edu.upeu.dad.taller.entity.Taller;

@Component
public class TallerMapper {

    public Taller toEntity(TallerRequest dto) {
        Taller e = new Taller();
        e.setCodigo(dto.getCodigo());
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setCupoMaximo(dto.getCupoMaximo());
        e.setInstructorId(dto.getInstructorId());
        e.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        return e;
    }

    public void updateEntity(Taller e, TallerRequest dto) {
        e.setCodigo(dto.getCodigo());
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setCupoMaximo(dto.getCupoMaximo());
        e.setInstructorId(dto.getInstructorId());
        if (dto.getEstado() != null) e.setEstado(dto.getEstado());
    }

    public TallerResponse toResponse(Taller e) {
        TallerResponse r = new TallerResponse();
        r.setId(e.getId());
        r.setCodigo(e.getCodigo());
        r.setNombre(e.getNombre());
        r.setDescripcion(e.getDescripcion());
        r.setCupoMaximo(e.getCupoMaximo());
        r.setInstructorId(e.getInstructorId());
        r.setEstado(e.getEstado());
        return r;
    }
}
