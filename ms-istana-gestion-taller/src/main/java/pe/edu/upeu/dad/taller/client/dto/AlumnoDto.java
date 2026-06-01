package pe.edu.upeu.dad.taller.client.dto;

// Representa la respuesta del microservicio de alumnos (consumida via Feign)
public class AlumnoDto {

    private Long id;
    private String codigo;
    private String nombres;
    private String apellidos;
    private String email;
    private Integer ciclo;
    private Boolean estado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getCiclo() { return ciclo; }
    public void setCiclo(Integer ciclo) { this.ciclo = ciclo; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
