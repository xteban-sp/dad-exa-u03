package pe.edu.upeu.dad.taller.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"taller_id", "alumno_id"}))
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "taller_id", nullable = false)
    private Long tallerId;

    // Referencia por ID al microservicio de alumnos (bajo acoplamiento)
    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;

    @Column(nullable = false)
    private LocalDateTime fechaInscripcion = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTallerId() { return tallerId; }
    public void setTallerId(Long tallerId) { this.tallerId = tallerId; }

    public Long getAlumnoId() { return alumnoId; }
    public void setAlumnoId(Long alumnoId) { this.alumnoId = alumnoId; }

    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
}
