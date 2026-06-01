package pe.edu.upeu.dad.alumno.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.dad.alumno.entity.Alumno;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    boolean existsByCodigo(String codigo);
    boolean existsByEmail(String email);
}
