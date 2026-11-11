//DTO REQUEST ESTE LLEGA DESDE EL FRONT
package cl.sonidovivo.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock debe ser 0 o mayor")
    private Integer Stock;

}

