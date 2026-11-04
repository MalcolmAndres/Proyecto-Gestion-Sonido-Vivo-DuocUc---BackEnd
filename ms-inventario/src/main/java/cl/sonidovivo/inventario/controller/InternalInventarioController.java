package cl.sonidovivo.inventario.controller;

import cl.sonidovivo.inventario.dto.PedidoIdRequest;
import cl.sonidovivo.inventario.dto.ReservaRequest;
import cl.sonidovivo.inventario.dto.StockDTO;
import cl.sonidovivo.inventario.service.InventarioService;
import cl.sonidovivo.inventario.service.ReservaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//Este Controller es para la comunicacion con otros microvervicios. No conectado al gateway, No lo publica.

@RestController
@RequestMapping("/internal/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario interno", description = "Usado por ms-catalogo y ms pedidos")
public class InternalInventarioController {
    private final InventarioService inventarioService;
    private final ReservaService reservaService;

    @GetMapping
    public List<StockDTO> stock(@RequestParam List<Long> ids) {
        return inventarioService.stockDe(ids);
    }

    @PostMapping("/reservar")
    public ResponseEntity<Void> reservar(@Valid @RequestBody ReservaRequest
                                                 request) {
        reservaService.reservar(request);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/descontar")
    public ResponseEntity<Void> descontar(@Valid @RequestBody PedidoIdRequest
                                                  request) {
        reservaService.descontar(request.getPedidoId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/liberar")
    public ResponseEntity<Void> liberar(@Valid @RequestBody PedidoIdRequest
                                                request) {
        reservaService.liberar(request.getPedidoId());
        return ResponseEntity.ok().build();
    }

}