package cl.sonidovivo.ubicaciones.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntoRetiroRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    @NotNull
    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    private Double latitud;
    @NotNull
    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    private Double longitud;
}