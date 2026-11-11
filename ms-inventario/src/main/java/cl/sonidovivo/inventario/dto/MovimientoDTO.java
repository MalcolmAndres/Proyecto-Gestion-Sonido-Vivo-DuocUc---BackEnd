package cl.sonidovivo.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    private Long id;
    private Long productoId;
    private Integer cantidad;
    private String tipo;
    private Long pedidoId;
    private LocalDateTime fecha;
}