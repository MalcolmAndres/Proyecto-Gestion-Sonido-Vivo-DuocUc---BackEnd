package cl.sonidovivo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// EXACTAMENTE lo que devolvía mockApi.login: { token, usuario: { id, nombre, email, rol, activo } }
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UsuarioDTO usuario;
}