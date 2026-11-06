package cl.sonidovivo.catalogo.service;

import cl.sonidovivo.catalogo.client.InventarioClient;
import cl.sonidovivo.catalogo.dto.ProductoDTO;
import cl.sonidovivo.catalogo.dto.ProductoInternoDTO;
import cl.sonidovivo.catalogo.dto.ProductoRequest;
import cl.sonidovivo.catalogo.dto.StockDTO;
import cl.sonidovivo.catalogo.exception.RecursoNoEncontradoException;
import cl.sonidovivo.catalogo.exception.SolicitudInvalidaException;
import cl.sonidovivo.catalogo.mapper.ProductoMapper;
import cl.sonidovivo.catalogo.model.Categoria;
import cl.sonidovivo.catalogo.model.Producto;
import cl.sonidovivo.catalogo.repository.CategoriaRepository;
import cl.sonidovivo.catalogo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final InventarioClient inventarioClient;
    private final ProductoMapper mapper;

    public List<ProductoDTO> buscar(String categoria, String busqueda) {
        String cat = null;
        if (categoria != null && !categoria.isBlank()) {
            cat = categoria;
        }
        String texto = null;
        if (busqueda != null && !busqueda.isBlank()) {
            texto = busqueda.trim();
        }
        List<Producto> productos = productoRepository.buscar(cat, texto);
        List<Long> ids = new ArrayList<>();
        for (Producto p : productos) {
            ids.add(p.getId());
        }
        Map<Long, Integer> stock = stockDe(ids);
        return productos.stream()
                .map(p -> mapper.toDTO(p, stock.getOrDefault(p.getId(), 0)))
                .collect(Collectors.toList());
    }
    public ProductoDTO obtener(Long id) {
        Producto producto = buscarPorId(id);
        return mapper.toDTO(producto, stockDe(List.of(id)).getOrDefault(id, 0));
    }

    @Transactional
    public ProductoDTO crear(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new SolicitudInvalidaException("Ya existe un producto con el código " + request.getCodigo());
        }
        Producto producto = new Producto();
        aplicarCambios(producto, request);
        return mapper.toDTO(productoRepository.save(producto), 0);
    }
    @Transactional
    public ProductoDTO actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarPorId(id);
        if (!producto.getCodigo().equals(request.getCodigo()) &&
                productoRepository.existsByCodigo(request.getCodigo())) {
            throw new SolicitudInvalidaException("Ya existe un producto con el código " + request.getCodigo());
        }
        aplicarCambios(producto, request);
        Producto guardado = productoRepository.save(producto);
        return mapper.toDTO(guardado, stockDe(List.of(id)).getOrDefault(id,
                0));
    }
    @Transactional
    public void desactivar(Long id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }
    public List<ProductoInternoDTO> buscarInternos(List<Long> ids) {
        return productoRepository.findByIdIn(ids).stream()
                .map(p -> new ProductoInternoDTO(p.getId(), p.getNombre(),
                        p.getPrecio(), p.getActivo()))
                .collect(Collectors.toList());
    }
    private Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    }
    private void aplicarCambios(Producto producto, ProductoRequest request) {
        Categoria categoria =
                categoriaRepository.findByNombre(request.getCategoria())
                        .orElseThrow(() -> new SolicitudInvalidaException("La categoría '" + request.getCategoria() + "' no existe"));
        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setMarca(request.getMarca());
        producto.setModelo(request.getModelo());
        producto.setPrecio(request.getPrecio());
        producto.setDescripcion(request.getDescripcion());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setActivo(request.getActivo() == null ||
                request.getActivo());
        producto.setCategoria(categoria);
    }
    private Map<Long, Integer> stockDe(List<Long> ids) {
        Map<Long, Integer> resultado = new HashMap<>();
        if (ids.isEmpty()) {
            return resultado;
        }
        try {
            for (StockDTO s : inventarioClient.stockDe(ids)) {
                resultado.put(s.getProductoId(), s.getStock());
            }
        } catch (Exception e) {
            log.warn("No se pudo consultar ms-inventario: {}",
                    e.getMessage());
        }
        return resultado;
    }
}