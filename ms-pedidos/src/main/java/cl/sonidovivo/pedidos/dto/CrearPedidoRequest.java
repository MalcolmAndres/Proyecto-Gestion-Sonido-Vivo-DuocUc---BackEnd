package cl.sonidovivo.pedidos.dto;

import cl.sonidovivo.pedidos.enums.MetodoPago;
import cl.sonidovivo.pedidos.enums.TipoEntrega;
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
public class CrearPedidoRequest {
    @NotEmpty(message = "El pedido no contiene productos.")
    private List<@Valid ItemPedidoRequest> productos;

    @NotNull(message = "Debe seleccionar un método de pago.")
    private MetodoPago metodoPago;

    @NotNull(message = "Debe seleccionar el tipo de entrega.")
    private TipoEntrega tipoEntrega;

    private String direccion;
    private Long puntoRetiro;
}