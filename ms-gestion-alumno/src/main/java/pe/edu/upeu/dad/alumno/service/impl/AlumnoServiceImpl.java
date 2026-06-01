package pe.edu.upeu.dad.alumno.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.dad.alumno.dto.AlumnoRequest;
import pe.edu.upeu.dad.alumno.dto.AlumnoResponse;
import pe.edu.upeu.dad.alumno.entity.Alumno;
import pe.edu.upeu.dad.alumno.exception.BusinessException;
import pe.edu.upeu.dad.alumno.exception.ResourceNotFoundException;
import pe.edu.upeu.dad.alumno.mapper.AlumnoMapper;
import pe.edu.upeu.dad.alumno.repository.AlumnoRepository;
import pe.edu.upeu.dad.alumno.service.AlumnoService;

import java.util.List;

@Service
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository repository;
    private final AlumnoMapper mapper;

    public AlumnoServiceImpl(AlumnoRepository repository, AlumnoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoResponse obtenerPorId(Long id) {
        Alumno e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id " + id));
        return mapper.toResponse(e);
    }

    @Override
    @Transactional
    public AlumnoResponse crear(AlumnoRequest request) {
        if (repository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un alumno con el codigo " + request.getCodigo());
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un alumno con el email " + request.getEmail());
        }
        Alumno guardado = repository.save(mapper.toEntity(request));
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public AlumnoResponse actualizar(Long id, AlumnoRequest request) {
        Alumno e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con id " + id));
        mapper.updateEntity(e, request);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Alumno no encontrado con id " + id);
        }
        repository.deleteById(id);
    }
}
