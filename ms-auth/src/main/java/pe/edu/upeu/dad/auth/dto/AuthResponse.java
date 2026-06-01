package pe.edu.upeu.dad.auth.dto;

public class AuthResponse {

    private String token;
    private String tipo = "Bearer";
    private String username;
    private String rol;
    private long expiraEnSegundos;

    public AuthResponse(String token, String username, String rol, long expiraEnSegundos) {
        this.token = token;
        this.username = username;
        this.rol = rol;
        this.expiraEnSegundos = expiraEnSegundos;
    }

    public String getToken() { return token; }
    public String getTipo() { return tipo; }
    public String getUsername() { return username; }
    public String getRol() { return rol; }
    public long getExpiraEnSegundos() { return expiraEnSegundos; }
}
