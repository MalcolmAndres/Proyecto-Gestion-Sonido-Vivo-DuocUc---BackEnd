package cl.sonidovivo.inventario.controller;
import cl.sonidovivo.inventario.dto.MovimientoDTO;
import cl.sonidovivo.inventario.dto.StockDTO;
import cl.sonidovivo.inventario.dto.StockRequest;
import cl.sonidovivo.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequiredArgsConstructor
@Tag(name = "Inventario", description =  "Stock de los productos e historial de los movimientos")
public class InventarioController{
    private final InventarioService inventarioService;

    @Operation(summary = "Cambia el stock de un producto (ADMIN O VENDEDOR)")
    @PutMapping("/api/productos/{productoId}/stock")
    public StockDTO actualizarStock(@PathVariable Long productoId,
                                    @Valid  @RequestBody StockRequest request)
    {
        return inventarioService.actualizarStock(productoId, request.getStock());
    }

 @Operation(summary = "Historial de movimientos (de todos o de un producto)")
@GetMapping("/api/inventario/movimientos")
public List<MovimientoDTO> movimientos(@RequestParam(required = false)
                                       Long productoId) {
    return inventarioService.listarMovimientos(productoId);
}
}