package cl.sonidovivo.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long id;
    private Long usuarioId;
    private String clienteNombre;
    private List<DetalleDTO> productos;
    private Integer total;
    private String metodoPago;
    private String tipoEntrega;
    private String direccion;
    private Long puntoRetiro;
    private LocalDateTime fecha;
    private String estado;
}