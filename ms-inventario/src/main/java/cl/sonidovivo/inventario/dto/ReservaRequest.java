package cl.sonidovivo.inventario.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequest {
    @NotNull
    private Long pedidoId;
    @NotEmpty(message = "El pedido no tiene productos")
    private List<@Valid ItemReserva> items;
}