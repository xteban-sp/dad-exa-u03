package pe.edu.upeu.dad.instructor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.dad.instructor.entity.Instructor;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
}
