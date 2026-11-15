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
    @Size(max = 20, message = "El código es muy largo (máximo 20 caracteres)")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre es muy largo (máximo 150 caracteres)")
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca es muy larga (máximo 100 caracteres)")
    private String marca;

    @Size(max = 100, message = "El modelo es muy largo (máximo 100 caracteres)")
    private String modelo;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 60, message = "La categoría es muy larga (máximo 60 caracteres)")
    private String categoria;

    @NotNull(message ="El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Integer precio;

    @Size(max = 1000, message = "La cescripción es muy larga")
    private String descripcion;

    // Solo rutas del front (/img/...) o URLs https
    @Size(max = 500, message = "La URL de la imagen es muy larga")
    @Pattern(regexp = "^$|^(/|https://).*", message = "La imagen debe ser una ruta /img/... o una URL https")
    private String imagenUrl;
    private Boolean activo;
}