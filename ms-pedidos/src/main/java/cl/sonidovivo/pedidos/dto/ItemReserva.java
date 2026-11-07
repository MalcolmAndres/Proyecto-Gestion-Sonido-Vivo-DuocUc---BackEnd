package cl.sonidovivo.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemReserva {
    private Long productoId;
    private Integer cantidad;
}