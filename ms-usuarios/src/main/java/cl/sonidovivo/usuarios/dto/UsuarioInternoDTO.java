package cl.sonidovivo.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// SOLO para ms-auth: incluye el hash para comparar la clave. Nunca sale al navegador.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioInternoDTO {
    private Long id;
    private String nombre;
    private String email;
    private String passwordHash;
    private String rol;
    private Boolean activo;
}