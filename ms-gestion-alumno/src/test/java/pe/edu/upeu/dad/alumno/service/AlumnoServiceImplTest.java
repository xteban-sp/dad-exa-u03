package pe.edu.upeu.dad.alumno.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.dad.alumno.dto.AlumnoResponse;
import pe.edu.upeu.dad.alumno.entity.Alumno;
import pe.edu.upeu.dad.alumno.exception.BusinessException;
import pe.edu.upeu.dad.alumno.mapper.AlumnoMapper;
import pe.edu.upeu.dad.alumno.repository.AlumnoRepository;
import pe.edu.upeu.dad.alumno.service.impl.AlumnoServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceImplTest {

    @Mock AlumnoRepository repository;
    @Mock AlumnoMapper mapper;
    @InjectMocks AlumnoServiceImpl service;

    @Test
    void incrementarTaller_enElMaximo_lanzaBusinessException() {
        Alumno a = new Alumno();
        a.setId(1L);
        a.setTalleresInscritos(3); // ya en el maximo (3)
        when(repository.findById(1L)).thenReturn(Optional.of(a));

        assertThrows(BusinessException.class, () -> service.incrementarTaller(1L));
        verify(repository, never()).save(any());
    }

    @Test
    void incrementarTaller_normal_incrementaEnUno() {
        Alumno a = new Alumno();
        a.setId(1L);
        a.setTalleresInscritos(1);
        when(repository.findById(1L)).thenReturn(Optional.of(a));
        when(repository.save(any(Alumno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Alumno.class))).thenReturn(new AlumnoResponse());

        service.incrementarTaller(1L);

        assertEquals(2, a.getTalleresInscritos());
    }

    @Test
    void decrementarTaller_enCero_noBajaDeCero() {
        Alumno a = new Alumno();
        a.setId(1L);
        a.setTalleresInscritos(0);
        when(repository.findById(1L)).thenReturn(Optional.of(a));
        when(repository.save(any(Alumno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Alumno.class))).thenReturn(new AlumnoResponse());

        service.decrementarTaller(1L);

        assertEquals(0, a.getTalleresInscritos());
    }
}
