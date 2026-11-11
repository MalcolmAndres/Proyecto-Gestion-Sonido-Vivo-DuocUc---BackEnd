package cl.sonidovivo.inventario.repository;

import cl.sonidovivo.inventario.enums.EstadoReserva;
import cl.sonidovivo.inventario.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByPedidoIdAndEstado(Long pedidoId, EstadoReserva estado);
    boolean existsByPedidoId(Long pedidoId);
}