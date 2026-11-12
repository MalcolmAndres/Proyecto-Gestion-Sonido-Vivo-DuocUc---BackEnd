package cl.sonidovivo.pagos.service;

import cl.sonidovivo.pagos.client.PedidosClient;
import cl.sonidovivo.pagos.dto.ResultadoPagoRequest;
import cl.sonidovivo.pagos.enums.EstadoPago;
import cl.sonidovivo.pagos.model.Pago;
import cl.sonidovivo.pagos.repository.PagoRepository;
import cl.sonidovivo.pagos.webpay.WebpayClient;
import cl.sonidovivo.pagos.webpay.WebpayCommitResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock PagoRepository pagoRepository;
    @Mock PedidosClient pedidosClient;
    @Mock WebpayClient webpayClient;
    @InjectMocks PagoService pagoService;

    private Pago pago;

    @BeforeEach
    void preparar() {
        pago = new Pago();
        pago.setPedidoId(15L);
        pago.setToken("tok123");
        pago.setEstado(EstadoPago.PENDIENTE);
    }

    @Test
    void commitAprobadoAvisaAPedidosYMandaAlResultadoAprobado() {
        when(pagoRepository.findByToken("tok123")).thenReturn(Optional.of(pago));
        when(webpayClient.confirmar("tok123"))
                .thenReturn(new WebpayCommitResponse("SV15T1", 259980, "AUTHORIZED", "1213", 0));

        String destino = pagoService.procesarRetorno("tok123", null);

        assertEquals(EstadoPago.APROBADO, pago.getEstado());
        verify(pedidosClient).registrarPago(eq(15L), eq(new ResultadoPagoRequest(true, "1213")));
        assertTrue(destino.endsWith("/pago/resultado?pedido=15&estado=aprobado"));
    }

    @Test
    void tarjetaRechazadaDejaElPagoRechazado() {
        when(pagoRepository.findByToken("tok123")).thenReturn(Optional.of(pago));
        when(webpayClient.confirmar("tok123"))
                .thenReturn(new WebpayCommitResponse("SV15T1", 259980, "FAILED", "000000", -1));

        String destino = pagoService.procesarRetorno("tok123", null);

        assertEquals(EstadoPago.RECHAZADO, pago.getEstado());
        assertTrue(destino.endsWith("estado=rechazado"));
    }

    @Test
    void siElClienteAnulaNoSeHaceCommit() {
        when(pagoRepository.findByToken("tok123")).thenReturn(Optional.of(pago));

        pagoService.procesarRetorno(null, "tok123");

        assertEquals(EstadoPago.ANULADO, pago.getEstado());
        verify(webpayClient, never()).confirmar(any());
    }
}