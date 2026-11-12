package cl.sonidovivo.usuarios.controller;

import cl.sonidovivo.usuarios.dto.RegistroRequest;
import cl.sonidovivo.usuarios.dto.UsuarioDTO;
import cl.sonidovivo.usuarios.dto.UsuarioInternoDTO;
import cl.sonidovivo.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Solo para ms-auth. El gateway NO publica /internal/**
@RestController
@RequestMapping("/internal/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios interno", description = "Lo usa ms-auth")
public class InternalUsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/por-email")   // GET /internal/usuarios/por-email?email=cliente@correo.cl
    public UsuarioInternoDTO porEmail(@RequestParam String email) {
        return usuarioService.buscarInternoPorEmail(email);
    }

    @PostMapping                // POST /internal/usuarios  (registro de clientes)
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(request));
    }
}