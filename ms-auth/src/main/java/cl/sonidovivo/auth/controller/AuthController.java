package cl.sonidovivo.auth.controller;

import cl.sonidovivo.auth.dto.AuthResponse;
import cl.sonidovivo.auth.dto.LoginRequest;
import cl.sonidovivo.auth.dto.RegistroRequest;
import cl.sonidovivo.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login y registro: entregan el token JWT")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Iniciar sesión")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Registrarse como cliente")
    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registro(request));
    }
}