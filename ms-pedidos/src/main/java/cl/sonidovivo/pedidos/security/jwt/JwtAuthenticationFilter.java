package cl.sonidovivo.pedidos.security.jwt;

import cl.sonidovivo.pedidos.security.UsuarioToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Sin token: sigue como anónimo (SecurityConfig decide si la ruta lo permite)
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.leerClaims(header.substring(7));

            // Guardamos TODO lo que trae el token (id, nombre, email, rol) para usarlo en los services
            Object id = claims.get("id");
            UsuarioToken usuario = new UsuarioToken(
                    id == null ? null : ((Number) id).longValue(),
                    claims.get("nombre", String.class),
                    claims.getSubject(),
                    claims.get("rol", String.class));

            UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(
                    usuario, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())));
            SecurityContextHolder.getContext().setAuthentication(autenticacion);

        } catch (JwtException | IllegalArgumentException | ClassCastException e) {
            // Token vencido o falso → 401 (el front cierra la sesión)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"mensaje\":\"Sesión expirada o token inválido\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
