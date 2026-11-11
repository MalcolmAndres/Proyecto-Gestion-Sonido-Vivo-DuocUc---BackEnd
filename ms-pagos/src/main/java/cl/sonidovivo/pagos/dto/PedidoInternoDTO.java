package cl.sonidovivo.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoInternoDTO {
    private Long id;
    private Long usuarioId;
    private Integer total;
    private String estado;
}