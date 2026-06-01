package pe.edu.upeu.dad.taller.service.impl;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.dad.taller.client.AlumnoClient;
import pe.edu.upeu.dad.taller.client.InstructorClient;
import pe.edu.upeu.dad.taller.client.dto.AlumnoDto;
import pe.edu.upeu.dad.taller.client.dto.InstructorDto;
import pe.edu.upeu.dad.taller.dto.TallerDetalleResponse;
import pe.edu.upeu.dad.taller.dto.TallerRequest;
import pe.edu.upeu.dad.taller.dto.TallerResponse;
import pe.edu.upeu.dad.taller.entity.Inscripcion;
import pe.edu.upeu.dad.taller.entity.Taller;
import pe.edu.upeu.dad.taller.exception.BusinessException;
import pe.edu.upeu.dad.taller.exception.ResourceNotFoundException;
import pe.edu.upeu.dad.taller.mapper.TallerMapper;
import pe.edu.upeu.dad.taller.repository.InscripcionRepository;
import pe.edu.upeu.dad.taller.repository.TallerRepository;
import pe.edu.upeu.dad.taller.service.TallerService;

import java.util.ArrayList;
import java.util.List;

@Service
public class TallerServiceImpl implements TallerService {

    private static final Logger log = LoggerFactory.getLogger(TallerServiceImpl.class);

    private final TallerRepository repository;
    private final InscripcionRepository inscripcionRepository;
    private final TallerMapper mapper;
    private final InstructorClient instructorClient;
    private final AlumnoClient alumnoClient;

    public TallerServiceImpl(TallerRepository repository,
                             InscripcionRepository inscripcionRepository,
                             TallerMapper mapper,
                             InstructorClient instructorClient,
                             AlumnoClient alumnoClient) {
        this.repository = repository;
        this.inscripcionRepository = inscripcionRepository;
        this.mapper = mapper;
        this.instructorClient = instructorClient;
        this.alumnoClient = alumnoClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TallerResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TallerResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarTaller(id));
    }

    @Override
    @Transactional
    public TallerResponse crear(TallerRequest request) {
        if (repository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Ya existe un taller con el codigo " + request.getCodigo());
        }
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional
    public TallerResponse actualizar(Long id, TallerRequest request) {
        Taller e = buscarTaller(id);
        mapper.updateEntity(e, request);
        return mapper.toResponse(repository.save(e));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Taller no encontrado con id " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public TallerResponse asignarInstructor(Long idTaller, Long idInstructor) {
        Taller taller = buscarTaller(idTaller);
        // Valida que el instructor exista en su microservicio (via Feign)
        InstructorDto instructor = obtenerInstructor(idInstructor);
        if (instructor == null) {
            throw new ResourceNotFoundException("Instructor no encontrado con id " + idInstructor);
        }
        taller.setInstructorId(instructor.getId());
        return mapper.toResponse(repository.save(taller));
    }

    @Override
    @Transactional
    public TallerDetalleResponse inscribirAlumno(Long idTaller, Long idAlumno) {
        Taller taller = buscarTaller(idTaller);

        // Valida que el alumno exista en su microservicio (via Feign)
        AlumnoDto alumno = obtenerAlumno(idAlumno);
        if (alumno == null) {
            throw new ResourceNotFoundException("Alumno no encontrado con id " + idAlumno);
        }

        if (inscripcionRepository.existsByTallerIdAndAlumnoId(idTaller, idAlumno)) {
            throw new BusinessException("El alumno " + idAlumno + " ya esta inscrito en el taller " + idTaller);
        }

        long inscritos = inscripcionRepository.countByTallerId(idTaller);
        if (inscritos >= taller.getCupoMaximo()) {
            throw new BusinessException("El taller " + idTaller + " no tiene cupo disponible");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setTallerId(idTaller);
        inscripcion.setAlumnoId(idAlumno);
        inscripcionRepository.save(inscripcion);

        return construirDetalle(taller);
    }

    @Override
    @Transactional(readOnly = true)
    public TallerDetalleResponse obtenerDetalleCompleto(Long idTaller) {
        return construirDetalle(buscarTaller(idTaller));
    }

    // ---------- Saga de matricula (orquestacion + compensacion) ----------
    // NOTA: este metodo NO es @Transactional a proposito. Cada paso confirma su
    // propia transaccion (en su propia BD), por eso si un paso falla se necesita
    // una COMPENSACION explicita (no basta con un rollback ACID).
    @Override
    public TallerDetalleResponse matricularAlumno(Long idTaller, Long idAlumno) {
        Taller taller = buscarTaller(idTaller);

        // Validaciones previas (lectura)
        AlumnoDto alumno = obtenerAlumno(idAlumno);
        if (alumno == null) {
            throw new ResourceNotFoundException("Alumno no encontrado con id " + idAlumno);
        }
        if (inscripcionRepository.existsByTallerIdAndAlumnoId(idTaller, idAlumno)) {
            throw new BusinessException("El alumno " + idAlumno + " ya esta matriculado en el taller " + idTaller);
        }

        // === PASO 1 (remoto): incrementar el contador de talleres del alumno ===
        // Si el alumno alcanzo su maximo, este paso lanza excepcion y la Saga aborta
        // sin haber tocado nada local (no hay que compensar).
        log.info("[SAGA] Inicio matricula taller={} alumno={}", idTaller, idAlumno);
        alumnoClient.incrementarTaller(idAlumno);
        log.info("[SAGA] Paso 1 OK: contador del alumno {} incrementado", idAlumno);

        // === PASO 2 (local): crear la inscripcion validando cupo ===
        try {
            long inscritos = inscripcionRepository.countByTallerId(idTaller);
            if (inscritos >= taller.getCupoMaximo()) {
                throw new BusinessException("El taller " + idTaller + " no tiene cupo disponible");
            }
            Inscripcion inscripcion = new Inscripcion();
            inscripcion.setTallerId(idTaller);
            inscripcion.setAlumnoId(idAlumno);
            inscripcionRepository.save(inscripcion);
            log.info("[SAGA] Paso 2 OK: inscripcion creada. Matricula completada.");
        } catch (RuntimeException e) {
            // === COMPENSACION: deshacer el Paso 1 remoto ===
            log.warn("[SAGA] Paso 2 FALLO ({}). Compensando Paso 1...", e.getMessage());
            try {
                alumnoClient.decrementarTaller(idAlumno);
                log.info("[SAGA] Compensacion OK: contador del alumno {} revertido", idAlumno);
            } catch (RuntimeException ce) {
                log.error("[SAGA] Compensacion FALLO para alumno {}: {}", idAlumno, ce.getMessage());
            }
            throw new BusinessException("Matricula revertida (Saga): " + e.getMessage());
        }

        return construirDetalle(taller);
    }

    // ---------- Helpers ----------

    private Taller buscarTaller(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Taller no encontrado con id " + id));
    }

    private TallerDetalleResponse construirDetalle(Taller taller) {
        TallerDetalleResponse r = new TallerDetalleResponse();
        r.setId(taller.getId());
        r.setCodigo(taller.getCodigo());
        r.setNombre(taller.getNombre());
        r.setDescripcion(taller.getDescripcion());
        r.setCupoMaximo(taller.getCupoMaximo());
        r.setEstado(taller.getEstado());

        // Instructor asignado (via Feign), si existe
        if (taller.getInstructorId() != null) {
            r.setInstructor(obtenerInstructor(taller.getInstructorId()));
        }

        // Alumnos inscritos (via Feign por cada inscripcion)
        List<AlumnoDto> alumnos = new ArrayList<>();
        List<Inscripcion> inscripciones = inscripcionRepository.findByTallerId(taller.getId());
        for (Inscripcion ins : inscripciones) {
            AlumnoDto alumno = obtenerAlumno(ins.getAlumnoId());
            if (alumno != null) {
                alumnos.add(alumno);
            }
        }
        r.setAlumnosInscritos(alumnos);
        r.setTotalInscritos(inscripciones.size());
        r.setCupoDisponible(taller.getCupoMaximo() - inscripciones.size());
        return r;
    }

    private InstructorDto obtenerInstructor(Long id) {
        try {
            return instructorClient.obtenerPorId(id);
        } catch (FeignException.NotFound e) {
            return null;
        }
    }

    private AlumnoDto obtenerAlumno(Long id) {
        try {
            return alumnoClient.obtenerPorId(id);
        } catch (FeignException.NotFound e) {
            return null;
        }
    }
}
