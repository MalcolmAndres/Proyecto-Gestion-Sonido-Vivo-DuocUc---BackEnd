package cl.sonidovivo.carrito.controller;

import cl.sonidovivo.carrito.dto.ItemCarritoDTO;
import cl.sonidovivo.carrito.dto.ItemCarritoRequest;
import cl.sonidovivo.carrito.security.UsuarioToken;
import cl.sonidovivo.carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Carrito")
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/api/carrito")
    public List<ItemCarritoDTO> miCarrito() {
        return carritoService.miCarrito();
    }

    @PutMapping("/api/carrito/items")
    public List<ItemCarritoDTO> guardar(@Valid @RequestBody ItemCarritoRequest request) {
        return carritoService.guardar(request);
    }

    @DeleteMapping("/api/carrito/items/{productoId}")
    public List<ItemCarritoDTO> quitar(@PathVariable Long productoId) {
        return carritoService.quitar(productoId);
    }

    @DeleteMapping("/api/carrito")
    public ResponseEntity<Void> vaciarMiCarrito() {
        carritoService.vaciar(UsuarioToken.actual().getId());
        return ResponseEntity.noContent().build();
    }

    // Lo usa ms-pedidos después de una compra
    @DeleteMapping("/internal/carrito/{usuarioId}")
    public ResponseEntity<Void> vaciar(@PathVariable Long usuarioId) {
        carritoService.vaciar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}