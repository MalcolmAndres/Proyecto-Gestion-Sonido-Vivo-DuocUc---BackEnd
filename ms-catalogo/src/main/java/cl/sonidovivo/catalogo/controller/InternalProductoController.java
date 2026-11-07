package cl.sonidovivo.catalogo.controller;
import cl.sonidovivo.catalogo.dto.ProductoInternoDTO;
import cl.sonidovivo.catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/internal/productos")
@RequiredArgsConstructor
@Tag(name = "Productos interno", description = "Lo usa ms-pedidos para los precios reales")
public class InternalProductoController {
    private final ProductoService productoService;

    @GetMapping   // GET /internal/productos?ids=1,5
    public List<ProductoInternoDTO> porIds(@RequestParam List<Long> ids) {
        return productoService.buscarInternos(ids);
    }
}