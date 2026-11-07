package cl.sonidovivo.pagos.service;

import cl.sonidovivo.pagos.client.PedidosClient;
import cl.sonidovivo.pagos.dto.IniciarPagoResponse;
import cl.sonidovivo.pagos.dto.PedidoInternoDTO;
import cl.sonidovivo.pagos.dto.ResultadoPagoRequest;
import cl.sonidovivo.pagos.enums.EstadoPago;
import cl.sonidovivo.pagos.exception.RecursoNoEncontradoException;
import cl.sonidovivo.pagos.exception.SolicitudInvalidaException;
import cl.sonidovivo.pagos.model.Pago;
import cl.sonidovivo.pagos.repository.PagoRepository;
import cl.sonidovivo.pagos.security.UsuarioToken;
import cl.sonidovivo.pagos.webpay.WebpayClient;
import cl.sonidovivo.pagos.webpay.WebpayCommitResponse;
import cl.sonidovivo.pagos.webpay.WebpayCrearRequest;
import cl.sonidovivo.pagos.webpay.WebpayCrearResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidosClient pedidosClient;
    private final WebpayClient webpayClient;

    @Value("${webpay.return-url}")
    private String returnUrl;

    @Value("${front.url}")
    private String frontUrl;

    // ===== 1) El cliente quiere pagar un pedido =====
    @Transactional
    public IniciarPagoResponse iniciar(Long pedidoId) {
        UsuarioToken cliente = UsuarioToken.actual();

        PedidoInternoDTO pedido;
        try {
            pedido = pedidosClient.obtener(pedidoId);
        } catch (FeignException e) {
            throw new RecursoNoEncontradoException("Pedido no encontrado");
        }

        if (!pedido.getUsuarioId().equals(cliente.getId())) {
            throw new SolicitudInvalidaException("Ese pedido no es tuyo");
        }
        if (!"PENDIENTE_PAGO".equals(pedido.getEstado())) {
            throw new SolicitudInvalidaException("El pedido no está esperando pago");
        }

        // buy_order: único y de máximo 26 caracteres
        String buyOrder = "SV" + pedidoId + "T" + (System.currentTimeMillis() % 1_000_000_000L);

        WebpayCrearResponse respuesta = webpayClient.crear(
                new WebpayCrearRequest(buyOrder, String.valueOf(cliente.getId()), pedido.getTotal(), returnUrl));

        Pago pago = new Pago();
        pago.setPedidoId(pedidoId);
        pago.setMonto(pedido.getTotal());
        pago.setBuyOrder(buyOrder);
        pago.setSessionId(String.valueOf(cliente.getId()));
        pago.setToken(respuesta.getToken());
        pago.setEstado(EstadoPago.PENDIENTE);
        pagoRepository.save(pago);

        log.info("Pago iniciado: pedido {} por ${}", pedidoId, pedido.getTotal());
        return new IniciarPagoResponse(respuesta.getToken(), respuesta.getUrl());
    }

    // ===== 2) Webpay devuelve al cliente =====
    @Transactional
    public String procesarRetorno(String tokenWs, String tbkToken) {
        // El cliente apretó "Anular"
        if (tbkToken != null && !tbkToken.isBlank()) {
            Pago pago = buscarPorToken(tbkToken);
            return cerrar(pago, EstadoPago.ANULADO);
        }

        // Flujo normal: hay que confirmar (commit)
        if (tokenWs != null && !tokenWs.isBlank()) {
            Pago pago = buscarPorToken(tokenWs);
            EstadoPago resultado;
            try {
                WebpayCommitResponse commit = webpayClient.confirmar(tokenWs);
                pago.setCodigoAutorizacion(commit.getAuthorizationCode());
                resultado = commit.estaAprobado() ? EstadoPago.APROBADO : EstadoPago.RECHAZADO;
            } catch (Exception e) {
                log.error("Falló el commit en Webpay", e);
                resultado = EstadoPago.RECHAZADO;
            }
            return cerrar(pago, resultado);
        }

        // Tiempo agotado u otro caso
        return frontUrl + "/pago/resultado?estado=error";
    }

    private String cerrar(Pago pago, EstadoPago estado) {
        pago.setEstado(estado);
        pagoRepository.save(pago);

        boolean aprobado = estado == EstadoPago.APROBADO;
        pedidosClient.registrarPago(pago.getPedidoId(),
                new ResultadoPagoRequest(aprobado, pago.getCodigoAutorizacion()));

        return frontUrl + "/pago/resultado?pedido=" + pago.getPedidoId()
                + "&estado=" + (aprobado ? "aprobado" : "rechazado");
    }

    private Pago buscarPorToken(String token) {
        return pagoRepository.findByToken(token)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));
    }
}