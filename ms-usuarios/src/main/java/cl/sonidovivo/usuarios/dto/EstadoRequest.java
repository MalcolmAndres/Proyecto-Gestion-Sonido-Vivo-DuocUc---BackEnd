package cl.sonidovivo.usuarios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoRequest {
    @NotNull(message = "El estado es obligatorio")
    private Boolean activo;
}