package pe.edu.upeu.dad.taller.dto;

import pe.edu.upeu.dad.taller.client.dto.AlumnoDto;
import pe.edu.upeu.dad.taller.client.dto.InstructorDto;

import java.util.List;

// Respuesta compuesta: combina datos del taller con instructor y alumnos
// obtenidos de otros microservicios via Feign.
public class TallerDetalleResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer cupoMaximo;
    private Boolean estado;

    private InstructorDto instructor;
    private List<AlumnoDto> alumnosInscritos;
    private long totalInscritos;
    private int cupoDisponible;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(Integer cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public InstructorDto getInstructor() { return instructor; }
    public void setInstructor(InstructorDto instructor) { this.instructor = instructor; }

    public List<AlumnoDto> getAlumnosInscritos() { return alumnosInscritos; }
    public void setAlumnosInscritos(List<AlumnoDto> alumnosInscritos) { this.alumnosInscritos = alumnosInscritos; }

    public long getTotalInscritos() { return totalInscritos; }
    public void setTotalInscritos(long totalInscritos) { this.totalInscritos = totalInscritos; }

    public int getCupoDisponible() { return cupoDisponible; }
    public void setCupoDisponible(int cupoDisponible) { this.cupoDisponible = cupoDisponible; }
}
