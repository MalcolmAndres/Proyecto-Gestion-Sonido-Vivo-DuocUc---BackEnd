package cl.sonidovivo.catalogo.service;

import cl.sonidovivo.catalogo.client.InventarioClient;
import cl.sonidovivo.catalogo.dto.ProductoDTO;
import cl.sonidovivo.catalogo.dto.ProductoRequest;
import cl.sonidovivo.catalogo.dto.StockDTO;
import cl.sonidovivo.catalogo.exception.SolicitudInvalidaException;
import cl.sonidovivo.catalogo.mapper.ProductoMapper;
import cl.sonidovivo.catalogo.model.Categoria;
import cl.sonidovivo.catalogo.model.Producto;
import cl.sonidovivo.catalogo.repository.CategoriaRepository;
import cl.sonidovivo.catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock ProductoRepository productoRepository;
    @Mock CategoriaRepository categoriaRepository;
    @Mock InventarioClient inventarioClient;
    @Spy ProductoMapper mapper = new ProductoMapper();   // el mapper real
    @InjectMocks ProductoService productoService;
    private Producto guitarra() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Guitarras Acústicas");
        Producto p = new Producto();
        p.setId(1L);
        p.setCodigo("GA001");
        p.setNombre("Guitarra Acústica Folk");
        p.setMarca("Yamaha");
        p.setPrecio(129990);
        p.setCategoria(categoria);
        return p;
    }
    @Test
    void buscarAgregaElStockQueDaInventario() {
        when(productoRepository.buscar(null,
                null)).thenReturn(List.of(guitarra()));
        when(inventarioClient.stockDe(List.of(1L))).thenReturn(List.of(new
                StockDTO(1L, 8)));
        List<ProductoDTO> resultado = productoService.buscar("", "");
        assertEquals(8, resultado.get(0).getStock());
        assertEquals("Guitarras Acústicas", resultado.get(0).getCategoria());
    }
    @Test
    void siInventarioFallaElCatalogoIgualRespondeConStockCero() {
        when(productoRepository.buscar(null,
                null)).thenReturn(List.of(guitarra()));
        when(inventarioClient.stockDe(anyList())).thenThrow(new
                RuntimeException("ms-inventario caído"));
        List<ProductoDTO> resultado = productoService.buscar(null, null);
        assertEquals(1, resultado.size());
        assertEquals(0, resultado.get(0).getStock());
    }
    @Test
    void noSePuedeCrearUnProductoConCodigoRepetido() {
        when(productoRepository.existsByCodigo("GA001")).thenReturn(true);
        ProductoRequest request = new ProductoRequest("GA001", "Otra",
                "Fender", null,
                "Guitarras Acústicas", 99990, null, null, true);
        assertThrows(SolicitudInvalidaException.class, () ->
                productoService.crear(request));
        verify(productoRepository, never()).save(any());
    }
}