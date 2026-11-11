package cl.sonidovivo.pedidos.client;

import cl.sonidovivo.pedidos.dto.PedidoIdRequest;
import cl.sonidovivo.pedidos.dto.ReservaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "MS-INVENTARIO")
public interface InventarioClient {

    @PostMapping("/internal/inventario/reservar")
    void reservar(@RequestBody ReservaRequest request);

    @PostMapping("/internal/inventario/descontar")
    void descontar(@RequestBody PedidoIdRequest request);

    @PostMapping("/internal/inventario/liberar")
    void liberar(@RequestBody PedidoIdRequest request);
}