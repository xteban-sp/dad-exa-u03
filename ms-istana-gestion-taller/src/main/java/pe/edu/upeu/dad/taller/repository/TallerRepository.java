package pe.edu.upeu.dad.taller.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.dad.taller.entity.Taller;

public interface TallerRepository extends JpaRepository<Taller, Long> {
    boolean existsByCodigo(String codigo);
}
