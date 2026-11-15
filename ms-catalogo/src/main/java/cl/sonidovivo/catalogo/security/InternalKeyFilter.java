package cl.sonidovivo.catalogo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Protege las rutas /internal/**: solo las pueden usar otros microservicios,
// que envían la clave compartida INTERNAL_KEY en el encabezado X-Internal-Key.
@Component
public class InternalKeyFilter extends OncePerRequestFilter {

    @Value("${internal.key}")
    private String clave;

    // Solo revisa las rutas /internal/**; el resto sigue igual
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!clave.equals(request.getHeader("X-Internal-Key"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }
}