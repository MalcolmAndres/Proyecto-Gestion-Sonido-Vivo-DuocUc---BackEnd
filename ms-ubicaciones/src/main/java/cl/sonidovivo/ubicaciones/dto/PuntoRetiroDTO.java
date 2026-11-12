package cl.sonidovivo.ubicaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Igual a v2/src/mocks/data/puntosRetiro.json
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntoRetiroDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private Double latitud;
    private Double longitud;
}