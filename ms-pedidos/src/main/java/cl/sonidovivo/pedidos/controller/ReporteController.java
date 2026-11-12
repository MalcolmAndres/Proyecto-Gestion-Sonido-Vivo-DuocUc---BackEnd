package cl.sonidovivo.pedidos.controller;

import cl.sonidovivo.pedidos.dto.ReporteDTO;
import cl.sonidovivo.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes")
public class ReporteController {

    private final PedidoService pedidoService;

    @GetMapping("/ventas")
    public ReporteDTO ventas(@RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pedidoService.reporte(desde, hasta);
    }
}