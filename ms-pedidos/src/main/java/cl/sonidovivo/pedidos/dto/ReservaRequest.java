package cl.sonidovivo.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequest {
    private Long pedidoId;
    private List<ItemReserva> items;
}