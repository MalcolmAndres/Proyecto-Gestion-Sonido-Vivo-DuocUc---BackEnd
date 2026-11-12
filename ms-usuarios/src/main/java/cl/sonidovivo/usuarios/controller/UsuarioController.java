package cl.sonidovivo.usuarios.controller;

import cl.sonidovivo.usuarios.dto.ActualizarUsuarioRequest;
import cl.sonidovivo.usuarios.dto.CrearUsuarioRequest;
import cl.sonidovivo.usuarios.dto.EstadoRequest;
import cl.sonidovivo.usuarios.dto.UsuarioDTO;
import cl.sonidovivo.usuarios.security.UsuarioToken;
import cl.sonidovivo.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Panel de administración (solo ADMIN, ver SecurityConfig)
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Panel de administración de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar usuarios")
    @GetMapping
    public List<UsuarioDTO> listar() {
        return usuarioService.listar();
    }

    @Operation(summary = "Crear usuario con cualquier rol")
    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @Operation(summary = "Editar usuario y asignar rol")
    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarUsuarioRequest request) {
        return usuarioService.actualizar(id, request);
    }

    @Operation(summary = "Activar o desactivar")
    @PatchMapping("/{id}/estado")
    public UsuarioDTO cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        String quienPide = UsuarioToken.actual().getEmail();
        return usuarioService.cambiarEstado(id, request.getActivo(), quienPide);
    }
}