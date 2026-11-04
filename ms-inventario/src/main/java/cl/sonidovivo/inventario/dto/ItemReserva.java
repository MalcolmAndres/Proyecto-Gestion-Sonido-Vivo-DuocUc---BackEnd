package cl.sonidovivo.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemReserva {
    @NotNull
    private Long productoId;
    @NotNull
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}