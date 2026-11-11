package cl.sonidovivo.usuarios.dto;

import cl.sonidovivo.usuarios.enums.Rol;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUsuarioRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    private String email;
    private String password;
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}