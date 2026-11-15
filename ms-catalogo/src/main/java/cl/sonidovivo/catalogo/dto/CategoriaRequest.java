package cl.sonidovivo.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre es muy largo (máximo 60 caracteres)")
    private String nombre;
}