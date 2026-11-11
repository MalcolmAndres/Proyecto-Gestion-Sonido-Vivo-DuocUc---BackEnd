package cl.sonidovivo.pagos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IniciarPagoRequest {
    @NotNull(message = "Falta el pedido")
    private Long pedidoId;
}