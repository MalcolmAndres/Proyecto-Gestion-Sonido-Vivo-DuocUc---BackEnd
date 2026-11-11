package cl.sonidovivo.catalogo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import  lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {
    @NotBlank(message ="El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;
    private String modelo;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotNull(message ="El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Integer precio;

    @Size(max = 1000, message = "La cescripción es muy larga")
    private String descripcion;
    private String imagenUrl;
    private Boolean activo;
}