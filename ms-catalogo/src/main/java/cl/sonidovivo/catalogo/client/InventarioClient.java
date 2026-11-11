package cl.sonidovivo.catalogo.client;

import cl.sonidovivo.catalogo.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@FeignClient(name ="MS-INVENTARIO")
public interface InventarioClient{

    @GetMapping("/internal/inventario")
    List<StockDTO> stockDe(@RequestParam("ids") List<Long> ids);
}