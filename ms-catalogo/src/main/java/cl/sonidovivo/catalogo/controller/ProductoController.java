package cl.sonidovivo.catalogo.controller;

import cl.sonidovivo.catalogo.dto.ProductoDTO;
import cl.sonidovivo.catalogo.dto.ProductoRequest;
import cl.sonidovivo.catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos",description = "Catálogo de instrumentos")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Productos activos, con filtro opcional de categoria y texto")
    @GetMapping
    public List<ProductoDTO> buscar(@RequestParam(required = false) String
                                            categoria,
                                    @RequestParam(required = false) String
                                            busqueda) {
        return productoService.buscar(categoria, busqueda);
    }

    @Operation(summary = "Ficha de un producto")
    @GetMapping("/{id}")
    public ProductoDTO obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @Operation(summary = "Crear producto (ADMIN)")
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @Operation(summary = "Editar producto (ADMIN)")
    @PutMapping("/{id}")
    public ProductoDTO actualizar(@PathVariable Long id, @Valid @RequestBody
    ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @Operation(summary = "Desactivar producto (ADMIN)")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}


