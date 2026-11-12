package cl.sonidovivo.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {
    private long cantidadPedidos;
    private long totalVentas;
    private List<PedidoDTO> pedidos;
}