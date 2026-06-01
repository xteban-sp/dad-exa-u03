package pe.edu.upeu.dad.taller.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.dad.taller.client.AlumnoClient;
import pe.edu.upeu.dad.taller.client.InstructorClient;
import pe.edu.upeu.dad.taller.client.dto.AlumnoDto;
import pe.edu.upeu.dad.taller.entity.Taller;
import pe.edu.upeu.dad.taller.exception.BusinessException;
import pe.edu.upeu.dad.taller.mapper.TallerMapper;
import pe.edu.upeu.dad.taller.repository.InscripcionRepository;
import pe.edu.upeu.dad.taller.repository.TallerRepository;
import pe.edu.upeu.dad.taller.service.impl.TallerServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TallerServiceImplTest {

    @Mock TallerRepository repository;
    @Mock InscripcionRepository inscripcionRepository;
    @Mock TallerMapper mapper;
    @Mock InstructorClient instructorClient;
    @Mock AlumnoClient alumnoClient;
    @InjectMocks TallerServiceImpl service;

    private Taller tallerConCupo(int cupo) {
        Taller t = new Taller();
        t.setId(1L);
        t.setCupoMaximo(cupo);
        return t;
    }

    @Test
    void matricular_sinCupo_compensaElPasoRemoto() {
        when(repository.findById(1L)).thenReturn(Optional.of(tallerConCupo(1)));
        when(alumnoClient.obtenerPorId(5L)).thenReturn(new AlumnoDto());
        when(inscripcionRepository.existsByTallerIdAndAlumnoId(1L, 5L)).thenReturn(false);
        // Paso 1 (remoto) OK
        when(alumnoClient.incrementarTaller(5L)).thenReturn(new AlumnoDto());
        // Paso 2 (local) falla: ya hay 1 inscrito y cupo = 1
        when(inscripcionRepository.countByTallerId(1L)).thenReturn(1L);

        assertThrows(BusinessException.class, () -> service.matricularAlumno(1L, 5L));

        // Se ejecuto la COMPENSACION del paso remoto
        verify(alumnoClient, times(1)).decrementarTaller(5L);
        // No se persistio ninguna inscripcion
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void matricular_conCupo_completaSinCompensar() {
        when(repository.findById(1L)).thenReturn(Optional.of(tallerConCupo(5)));
        when(alumnoClient.obtenerPorId(5L)).thenReturn(new AlumnoDto());
        when(inscripcionRepository.existsByTallerIdAndAlumnoId(1L, 5L)).thenReturn(false);
        when(alumnoClient.incrementarTaller(5L)).thenReturn(new AlumnoDto());
        when(inscripcionRepository.countByTallerId(1L)).thenReturn(0L);
        when(inscripcionRepository.findByTallerId(1L)).thenReturn(List.of());

        assertDoesNotThrow(() -> service.matricularAlumno(1L, 5L));

        verify(inscripcionRepository, times(1)).save(any());
        verify(alumnoClient, never()).decrementarTaller(any());
    }
}
