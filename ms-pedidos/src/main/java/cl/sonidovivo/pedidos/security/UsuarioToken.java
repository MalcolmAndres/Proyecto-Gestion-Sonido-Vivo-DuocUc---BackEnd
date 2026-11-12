package cl.sonidovivo.pedidos.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// Los datos del usuario que vienen dentro del token JWT
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioToken {
    private Long id;
    private String nombre;
    private String email;
    private String rol;

    // Devuelve el usuario de la petición actual (lo dejó ahí JwtAuthenticationFilter)
    public static UsuarioToken actual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UsuarioToken) {
            return (UsuarioToken) auth.getPrincipal();
        }
        throw new IllegalStateException("No hay un usuario autenticado en esta petición");
    }
}
