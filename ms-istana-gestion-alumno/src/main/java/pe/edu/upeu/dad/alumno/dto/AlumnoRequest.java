package pe.edu.upeu.dad.alumno.dto;

import jakarta.validation.constraints.*;

public class AlumnoRequest {

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es valido")
    private String email;

    @NotNull(message = "El ciclo es obligatorio")
    @Min(value = 1, message = "El ciclo minimo es 1")
    @Max(value = 12, message = "El ciclo maximo es 12")
    private Integer ciclo;

    private Boolean estado = true;

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
