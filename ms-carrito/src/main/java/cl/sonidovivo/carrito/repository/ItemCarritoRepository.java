package cl.sonidovivo.carrito.repository;

import cl.sonidovivo.carrito.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByUsuarioIdOrderByIdAsc(Long usuarioId);

    Optional<ItemCarrito> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId);

    @Transactional
    void deleteByUsuarioIdAndProductoId(Long usuarioId, Long productoId);

    @Transactional
    void deleteByUsuarioId(Long usuarioId);
}