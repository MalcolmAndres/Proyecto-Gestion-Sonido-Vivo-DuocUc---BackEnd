package cl.sonidovivo.pedidos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoRequest {
    @NotNull
    private Long id;

    @NotNull
    @Positive(message = "La cantidad de productos no es válida.")
    private Integer cantidad;
}