package cl.sonidovivo.carrito.service;

import cl.sonidovivo.carrito.dto.ItemCarritoDTO;
import cl.sonidovivo.carrito.dto.ItemCarritoRequest;
import cl.sonidovivo.carrito.model.ItemCarrito;
import cl.sonidovivo.carrito.repository.ItemCarritoRepository;
import cl.sonidovivo.carrito.security.UsuarioToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final ItemCarritoRepository repository;

    public List<ItemCarritoDTO> miCarrito() {
        return repository.findByUsuarioIdOrderByIdAsc(UsuarioToken.actual().getId()).stream()
                .map(i -> new ItemCarritoDTO(i.getProductoId(), i.getCantidad()))
                .collect(Collectors.toList());
    }

    // Si el producto ya está, reemplaza la cantidad; si no, lo agrega
    @Transactional
    public List<ItemCarritoDTO> guardar(ItemCarritoRequest request) {
        Long usuarioId = UsuarioToken.actual().getId();

        ItemCarrito item = repository.findByUsuarioIdAndProductoId(usuarioId, request.getProductoId())
                .orElse(null);

        if (item == null) { // no estaba: se crea
            item = new ItemCarrito();
            item.setUsuarioId(usuarioId);
            item.setProductoId(request.getProductoId());
        }
        item.setCantidad(request.getCantidad());
        repository.save(item);

        return miCarrito();
    }

    @Transactional
    public List<ItemCarritoDTO> quitar(Long productoId) {
        repository.deleteByUsuarioIdAndProductoId(UsuarioToken.actual().getId(), productoId);
        return miCarrito();
    }

    @Transactional
    public void vaciar(Long usuarioId) {
        repository.deleteByUsuarioId(usuarioId);
    }
}