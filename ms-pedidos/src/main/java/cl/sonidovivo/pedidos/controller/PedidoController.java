package cl.sonidovivo.pedidos.controller;

import cl.sonidovivo.pedidos.dto.CrearPedidoRequest;
import cl.sonidovivo.pedidos.dto.EstadoRequest;
import cl.sonidovivo.pedidos.dto.PedidoDTO;
import cl.sonidovivo.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Crear pedido (CLIENTE)")
    @PostMapping
    public ResponseEntity<PedidoDTO> crear(@Valid @RequestBody CrearPedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(request));
    }

    @Operation(summary = "Pedidos del cliente que inició sesión")
    @GetMapping("/mis-pedidos")
    public List<PedidoDTO> misPedidos() {
        return pedidoService.misPedidos();
    }

    @Operation(summary = "Todos los pedidos (VENDEDOR o ADMIN)")
    @GetMapping
    public List<PedidoDTO> todos() {
        return pedidoService.todos();
    }

    @Operation(summary = "Avanzar el estado (VENDEDOR o ADMIN)")
    @PatchMapping("/{id}/estado")
    public PedidoDTO cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return pedidoService.cambiarEstado(id, request.getEstado());
    }
}