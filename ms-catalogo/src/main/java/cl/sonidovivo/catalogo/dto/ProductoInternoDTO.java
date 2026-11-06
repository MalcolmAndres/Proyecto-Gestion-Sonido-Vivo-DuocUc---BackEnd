package cl.sonidovivo.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoInternoDTO {
    private Long id;
    private String nombre;
    private Integer precio;
    private Boolean activo;
}