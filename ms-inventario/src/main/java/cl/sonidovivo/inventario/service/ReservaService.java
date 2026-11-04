package cl.sonidovivo.inventario.service;

import cl.sonidovivo.inventario.dto.ItemReserva;
import cl.sonidovivo.inventario.dto.ReservaRequest;
import cl.sonidovivo.inventario.enums.EstadoReserva;
import cl.sonidovivo.inventario.enums.TipoMovimiento;
import cl.sonidovivo.inventario.exception.RecursoNoEncontradoException;
import cl.sonidovivo.inventario.exception.SolicitudInvalidaException;
import cl.sonidovivo.inventario.exception.StockInsuficienteException;
import cl.sonidovivo.inventario.model.Inventario;
import cl.sonidovivo.inventario.model.Reserva;
import cl.sonidovivo.inventario.repository.InventarioRepository;
import cl.sonidovivo.inventario.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final InventarioRepository inventarioRepository;
    private final ReservaRepository reservaRepository;
    private final InventarioService inventarioService;

    @Transactional
    public void reservar(ReservaRequest request) {
        Long pedidoId = request.getPedidoId();
        if (reservaRepository.existsByPedidoId(pedidoId)) {
            throw new SolicitudInvalidaException("El pedido " + pedidoId + " ya tiene una reserva");
        }
        for (ItemReserva item : request.getItems()) {
            Inventario inventario = buscar(item.getProductoId());
            if (inventario.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto " + item.getProductoId() + ". Disponibles: " + inventario.getStock());
            }
        }
        for (ItemReserva item : request.getItems()) {
            Inventario inventario = buscar(item.getProductoId());
            inventario.setStock(inventario.getStock() - item.getCantidad());
            inventarioRepository.save(inventario);
            Reserva reserva = new Reserva();
            reserva.setPedidoId(pedidoId);
            reserva.setProductoId(item.getProductoId());
            reserva.setCantidad(item.getCantidad());
            reserva.setEstado(EstadoReserva.RESERVADA);
            reservaRepository.save(reserva);
            inventarioService.registrarMovimiento(item.getProductoId(), -item.getCantidad(),
                    TipoMovimiento.RESERVA, pedidoId);
        }
        log.info("Reserva creada para el pedido {}", pedidoId);
    }

    @Transactional
    public void descontar(Long pedidoId) {
        for (Reserva reserva : reservasActivas(pedidoId)) {
            reserva.setEstado(EstadoReserva.DESCONTADA);
            reservaRepository.save(reserva);
        }
    }

    @Transactional
    public void liberar(Long pedidoId) {
        for (Reserva reserva : reservasActivas(pedidoId)) {
            Inventario inventario = buscar(reserva.getProductoId());
            inventario.setStock(inventario.getStock() + reserva.getCantidad());
            inventarioRepository.save(inventario);

            reserva.setEstado(EstadoReserva.LIBERADA);
            reservaRepository.save(reserva);

            inventarioService.registrarMovimiento(reserva.getProductoId(), reserva.getCantidad(),
                    TipoMovimiento.LIBERACION, pedidoId);
        }
    }

    private Inventario buscar(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new StockInsuficienteException("El producto " + productoId + " no tiene stock"));
    }

    private List<Reserva> reservasActivas(Long pedidoId) {
        List<Reserva> reservas = reservaRepository.findByPedidoIdAndEstado(pedidoId, EstadoReserva.RESERVADA);
        if (reservas.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay reservas activas para el pedido " + pedidoId);
        }
        return reservas;
    }
}

