package cl.sonidovivo.inventario.service;

import cl.sonidovivo.inventario.dto.ItemReserva;
import cl.sonidovivo.inventario.dto.ReservaRequest;
import cl.sonidovivo.inventario.enums.EstadoReserva;
import cl.sonidovivo.inventario.exception.StockInsuficienteException;
import cl.sonidovivo.inventario.model.Inventario;
import cl.sonidovivo.inventario.model.Reserva;
import cl.sonidovivo.inventario.repository.InventarioRepository;
import cl.sonidovivo.inventario.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {
    @Mock InventarioRepository inventarioRepository;
    @Mock ReservaRepository reservaRepository;
    @Mock InventarioService inventarioService;
    @InjectMocks ReservaService reservaService;
    private Inventario inventario(Long productoId, int stock) {
        Inventario i = new Inventario();
        i.setProductoId(productoId);
        i.setStock(stock);
        return i;
    }

    @Test
    void reservarRestaElStockYGuardaLaReserva() {
        Inventario guitarra = inventario(1L, 8);
        when(reservaRepository.existsByPedidoId(15L)).thenReturn(false);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(guitarra));
        reservaService.reservar(new ReservaRequest(15L, List.of(new
                ItemReserva(1L, 2))));
        assertEquals(6, guitarra.getStock());
        verify(reservaRepository).save(any(Reserva.class));
    }
    @Test
    void reservarSinStockSuficienteLanzaErrorYNoGuardaNada() {
        when(reservaRepository.existsByPedidoId(16L)).thenReturn(false);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario(1L, 1)));
        assertThrows(StockInsuficienteException.class, () ->
                reservaService.reservar(new ReservaRequest(16L, List.of(new
                        ItemReserva(1L, 5)))));
        verify(reservaRepository, never()).save(any());
    }
    @Test
    void liberarDevuelveLasUnidadesAlStock() {
        Inventario guitarra = inventario(1L, 6);
        Reserva reserva = new Reserva();
        reserva.setPedidoId(15L);
        reserva.setProductoId(1L);
        reserva.setCantidad(2);
        reserva.setEstado(EstadoReserva.RESERVADA);
        when(reservaRepository.findByPedidoIdAndEstado(15L,
                EstadoReserva.RESERVADA)).thenReturn(List.of(reserva));
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(guitarra));
        reservaService.liberar(15L);
        assertEquals(8, guitarra.getStock());
        assertEquals(EstadoReserva.LIBERADA, reserva.getEstado());
    }
}

