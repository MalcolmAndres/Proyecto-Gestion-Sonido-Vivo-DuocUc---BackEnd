package cl.sonidovivo.pedidos.client;

import cl.sonidovivo.pedidos.dto.ProductoInternoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "MS-CATALOGO")
public interface CatalogoClient {

    @GetMapping("/internal/productos")
    List<ProductoInternoDTO> buscar(@RequestParam("ids") List<Long> ids);
}