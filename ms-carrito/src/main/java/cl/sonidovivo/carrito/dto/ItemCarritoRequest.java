package cl.sonidovivo.carrito.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// { "productoId": 1, "cantidad": 2 }
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoRequest {
    @NotNull
    private Long productoId;

    @NotNull
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}