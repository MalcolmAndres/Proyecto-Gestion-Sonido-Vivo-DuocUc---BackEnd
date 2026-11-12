package cl.sonidovivo.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoPagoRequest {
    private Boolean aprobado;
    private String codigoAutorizacion;
}