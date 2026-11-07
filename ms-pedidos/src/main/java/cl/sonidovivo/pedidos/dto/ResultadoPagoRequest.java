package cl.sonidovivo.pedidos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoPagoRequest {
    @NotNull
    private Boolean aprobado;
    private String codigoAutorizacion;
}