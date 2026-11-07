package cl.sonidovivo.pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleDTO {
    private Long id;
    private String nombre;
    private Integer precio;
    private Integer cantidad;
}