package pe.edu.upeu.dad.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.upeu.dad.auth.entity.Rol;
import pe.edu.upeu.dad.auth.entity.Usuario;
import pe.edu.upeu.dad.auth.repository.UsuarioRepository;

// Crea usuarios de ejemplo al iniciar (uno por rol) si no existen.
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedUsuarios(UsuarioRepository repository, PasswordEncoder encoder) {
        return args -> {
            crear(repository, encoder, "admin", "admin123", Rol.ADMIN);
            crear(repository, encoder, "instructor1", "inst123", Rol.INSTRUCTOR);
            crear(repository, encoder, "alumno1", "alum123", Rol.ALUMNO);
        };
    }

    private void crear(UsuarioRepository repository, PasswordEncoder encoder,
                       String username, String password, Rol rol) {
        if (!repository.existsByUsername(username)) {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setPassword(encoder.encode(password));
            u.setRol(rol);
            u.setEstado(true);
            repository.save(u);
        }
    }
}
