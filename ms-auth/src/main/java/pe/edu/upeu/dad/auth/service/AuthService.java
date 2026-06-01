package pe.edu.upeu.dad.auth.service;

import pe.edu.upeu.dad.auth.dto.AuthResponse;
import pe.edu.upeu.dad.auth.dto.LoginRequest;
import pe.edu.upeu.dad.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse registrar(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
