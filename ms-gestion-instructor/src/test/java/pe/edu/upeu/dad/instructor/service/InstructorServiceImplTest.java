package pe.edu.upeu.dad.instructor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.dad.instructor.dto.InstructorRequest;
import pe.edu.upeu.dad.instructor.entity.Instructor;
import pe.edu.upeu.dad.instructor.exception.BusinessException;
import pe.edu.upeu.dad.instructor.exception.ResourceNotFoundException;
import pe.edu.upeu.dad.instructor.mapper.InstructorMapper;
import pe.edu.upeu.dad.instructor.repository.InstructorRepository;
import pe.edu.upeu.dad.instructor.service.impl.InstructorServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServiceImplTest {

    @Mock InstructorRepository repository;
    @Mock InstructorMapper mapper;
    @InjectMocks InstructorServiceImpl service;

    @Test
    void crear_conDniDuplicado_lanzaBusinessException() {
        InstructorRequest req = new InstructorRequest();
        req.setDni("70123456");
        req.setEmail("a@upeu.edu.pe");
        when(repository.existsByDni("70123456")).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.crear(req));
        verify(repository, never()).save(any());
    }

    @Test
    void obtenerPorId_inexistente_lanzaResourceNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void eliminar_inexistente_lanzaResourceNotFound() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(99L));
        verify(repository, never()).deleteById(any());
    }
}
