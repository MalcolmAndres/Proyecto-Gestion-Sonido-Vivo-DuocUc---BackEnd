package cl.sonidovivo.pedidos.controller;

import cl.sonidovivo.pedidos.dto.PedidoDTO;
import cl.sonidovivo.pedidos.dto.PedidoInternoDTO;
import cl.sonidovivo.pedidos.dto.ResultadoPagoRequest;
import cl.sonidovivo.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos interno", description = "Lo usa ms-pagos")
public class InternalPedidoController {

    private final PedidoService pedidoService;

    @GetMapping("/{id}")
    public PedidoInternoDTO obtener(@PathVariable Long id) {
        return pedidoService.interno(id);
    }

    @PostMapping("/{id}/pago")
    public PedidoDTO registrarPago(@PathVariable Long id, @Valid @RequestBody ResultadoPagoRequest request) {
        return pedidoService.registrarPago(id, request.getAprobado());
    }
}