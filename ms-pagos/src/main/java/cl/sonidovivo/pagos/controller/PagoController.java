package cl.sonidovivo.pagos.controller;

import cl.sonidovivo.pagos.dto.IniciarPagoRequest;
import cl.sonidovivo.pagos.dto.IniciarPagoResponse;
import cl.sonidovivo.pagos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Webpay Plus (ambiente de pruebas)")
public class PagoController {

    private final PagoService pagoService;

    @Value("${front.url}")
    private String frontUrl;

    @Operation(summary = "Crear la transacción en Webpay (CLIENTE)")
    @PostMapping("/iniciar")
    public IniciarPagoResponse iniciar(@Valid @RequestBody IniciarPagoRequest request) {
        return pagoService.iniciar(request.getPedidoId());
    }

    // Aquí vuelve el navegador desde Webpay (sin token JWT), por GET o POST
    @Operation(summary = "Retorno desde Webpay (lo llama Webpay, no el front)")
    @RequestMapping(value = "/retorno", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Void> retorno(@RequestParam(name = "token_ws", required = false) String tokenWs,
                                        @RequestParam(name = "TBK_TOKEN", required = false) String tbkToken) {
        String destino;
        try {
            destino = pagoService.procesarRetorno(tokenWs, tbkToken);
        } catch (Exception e) {
            log.error("Error procesando el retorno de Webpay", e);
            destino = frontUrl + "/pago/resultado?estado=error";
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(destino)).build();
    }
}