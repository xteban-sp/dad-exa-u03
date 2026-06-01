package pe.edu.upeu.dad.instructor.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.dad.instructor.dto.InstructorRequest;
import pe.edu.upeu.dad.instructor.dto.InstructorResponse;
import pe.edu.upeu.dad.instructor.entity.Instructor;
import pe.edu.upeu.dad.instructor.exception.BusinessException;
import pe.edu.upeu.dad.instructor.exception.ResourceNotFoundException;
import pe.edu.upeu.dad.instructor.mapper.InstructorMapper;
import pe.edu.upeu.dad.instructor.repository.InstructorRepository;
import pe.edu.upeu.dad.instructor.service.InstructorService;

import java.util.List;

@Service
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository repository;
    private final InstructorMapper mapper;

    public InstructorServiceImpl(InstructorRepository repository, InstructorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstructorResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorResponse obtenerPorId(Long id) {
        Instructor e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor no encontrado con id " + id));
        return mapper.toResponse(e);
    }

    @Override
    @Transactional
    public InstructorResponse crear(InstructorRequest request) {
        if (repository.existsByDni(request.getDni())) {
            throw new BusinessException("Ya existe un instructor con el DNI " + request.getDni());
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un instructor con el email " + request.getEmail());
        }
        Instructor guardado = repository.save(mapper.toEntity(request));
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public InstructorResponse actualizar(Long id, InstructorRequest request) {
        Instructor e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor no encontrado con id " + id));
        mapper.updateEntity(e, request);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Instructor no encontrado con id " + id);
        }
        repository.deleteById(id);
    }
}
