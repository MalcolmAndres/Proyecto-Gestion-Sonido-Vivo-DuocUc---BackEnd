package cl.sonidovivo.ubicaciones.controller;

import cl.sonidovivo.ubicaciones.dto.PuntoRetiroDTO;
import cl.sonidovivo.ubicaciones.dto.PuntoRetiroRequest;
import cl.sonidovivo.ubicaciones.service.PuntoRetiroService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puntos-retiro")
@RequiredArgsConstructor
@Tag(name = "Puntos de retiro", description = "Tienda y puntos para el mapa")
public class PuntoRetiroController {

    private final PuntoRetiroService service;

    @GetMapping
    public List<PuntoRetiroDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public PuntoRetiroDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<PuntoRetiroDTO> crear(@Valid @RequestBody PuntoRetiroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public PuntoRetiroDTO actualizar(@PathVariable Long id, @Valid @RequestBody PuntoRetiroRequest request) {
        return service.actualizar(id, request);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}