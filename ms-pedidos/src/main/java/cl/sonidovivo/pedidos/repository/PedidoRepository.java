package cl.sonidovivo.pedidos.repository;

import cl.sonidovivo.pedidos.enums.EstadoPedido;
import cl.sonidovivo.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<Pedido> findAllByOrderByFechaDesc();

    List<Pedido> findByFechaBetweenAndEstadoNotInOrderByFechaAsc(LocalDateTime desde,
                                                                 LocalDateTime hasta,
                                                                 List<EstadoPedido> excluidos);
}