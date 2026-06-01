package pe.edu.upeu.dad.auth.controller;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.dad.auth.dto.AuthResponse;
import pe.edu.upeu.dad.auth.dto.LoginRequest;
import pe.edu.upeu.dad.auth.dto.RegisterRequest;
import pe.edu.upeu.dad.auth.exception.UnauthorizedException;
import pe.edu.upeu.dad.auth.security.JwtService;
import pe.edu.upeu.dad.auth.service.AuthService;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Autenticacion", description = "Registro, login y validacion de JWT")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Registrar un nuevo usuario y devolver su token")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.registrar(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Autenticar usuario y emitir JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Validar un JWT y devolver sus datos (username y rol)")
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validar(@RequestHeader("Authorization") String authorization) {
        String token = extraerToken(authorization);
        Claims claims = jwtService.validarYObtenerClaims(token);
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("valido", true);
        resultado.put("username", claims.getSubject());
        resultado.put("rol", claims.get("rol"));
        resultado.put("expira", claims.getExpiration());
        return ResponseEntity.ok(resultado);
    }

    private String extraerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("Falta el header Authorization tipo Bearer");
        }
        return authorization.substring(7);
    }
}
