package cl.sonidovivo.usuarios.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtService {

    private final Key clave;

    // La clave llega de jwt.secret=${JWT_SECRET} (nunca escrita en el código)
    public JwtService(@Value("${jwt.secret}") String secreto) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
    }

    // Lanza excepción si el token está vencido, mal firmado o mal formado
    public Claims leerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(clave)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
