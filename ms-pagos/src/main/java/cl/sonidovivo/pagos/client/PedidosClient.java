package cl.sonidovivo.pagos.client;

import cl.sonidovivo.pagos.dto.PedidoInternoDTO;
import cl.sonidovivo.pagos.dto.ResultadoPagoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "MS-PEDIDOS")
public interface PedidosClient {

    @GetMapping("/internal/pedidos/{id}")
    PedidoInternoDTO obtener(@PathVariable("id") Long id);

    @PostMapping("/internal/pedidos/{id}/pago")
    void registrarPago(@PathVariable("id") Long id, @RequestBody ResultadoPagoRequest request);
}