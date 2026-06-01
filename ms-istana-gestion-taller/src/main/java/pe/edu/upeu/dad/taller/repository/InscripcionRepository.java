package pe.edu.upeu.dad.taller.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.dad.taller.entity.Inscripcion;

import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByTallerId(Long tallerId);
    boolean existsByTallerIdAndAlumnoId(Long tallerId, Long alumnoId);
    long countByTallerId(Long tallerId);
}
