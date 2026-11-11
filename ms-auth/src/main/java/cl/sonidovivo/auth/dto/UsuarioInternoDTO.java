package cl.sonidovivo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Igual al de ms-usuarios: trae el hash para comparar
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