package pe.edu.upeu.dad.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.upeu.dad.auth.dto.LoginRequest;
import pe.edu.upeu.dad.auth.dto.RegisterRequest;
import pe.edu.upeu.dad.auth.entity.Rol;
import pe.edu.upeu.dad.auth.entity.Usuario;
import pe.edu.upeu.dad.auth.exception.BusinessException;
import pe.edu.upeu.dad.auth.exception.UnauthorizedException;
import pe.edu.upeu.dad.auth.repository.UsuarioRepository;
import pe.edu.upeu.dad.auth.security.JwtService;
import pe.edu.upeu.dad.auth.service.impl.AuthServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock UsuarioRepository repository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks AuthServiceImpl service;

    @Test
    void login_passwordIncorrecta_lanzaUnauthorized() {
        Usuario u = new Usuario();
        u.setUsername("admin");
        u.setPassword("hash");
        u.setRol(Rol.ADMIN);
        u.setEstado(true);
        when(repository.findByUsername("admin")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("mala");

        assertThrows(UnauthorizedException.class, () -> service.login(req));
    }

    @Test
    void registrar_usernameDuplicado_lanzaBusinessException() {
        when(repository.existsByUsername("admin")).thenReturn(true);
        RegisterRequest req = new RegisterRequest();
        req.setUsername("admin");
        req.setPassword("x");
        req.setRol(Rol.ADMIN);

        assertThrows(BusinessException.class, () -> service.registrar(req));
        verify(repository, never()).save(any());
    }

    @Test
    void login_credencialesValidas_devuelveToken() {
        Usuario u = new Usuario();
        u.setUsername("admin");
        u.setPassword("hash");
        u.setRol(Rol.ADMIN);
        u.setEstado(true);
        when(repository.findByUsername("admin")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("admin123", "hash")).thenReturn(true);
        when(jwtService.generarToken("admin", "ADMIN")).thenReturn("token-jwt");
        when(jwtService.getExpirationSegundos()).thenReturn(3600L);

        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        var resp = service.login(req);
        assertEquals("token-jwt", resp.getToken());
        assertEquals("ADMIN", resp.getRol());
    }
}
