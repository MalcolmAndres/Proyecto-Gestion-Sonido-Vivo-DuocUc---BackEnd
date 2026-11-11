package cl.sonidovivo.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String marca;
    private String modelo;
    private String categoria;
    private Integer precio;
    private Integer stock;
    private String descripcion;
    private String imagenUrl;
    private Boolean activo;
}
