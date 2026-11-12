package cl.sonidovivo.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

// ms-auth es el ÚNICO que FIRMA tokens. Los demás solo los leen con la misma JWT_SECRET.
@Service
public class JwtService {

    private final Key clave;
    private final long duracionMs;

    public JwtService(@Value("${jwt.secret}") String secreto,
                      @Value("${jwt.duracion-horas:8}") long horas) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
        this.duracionMs = horas * 60 * 60 * 1000;
    }

    // El contrato del token: sub = email, y los claims id, nombre y rol
    public String generar(Long id, String nombre, String email, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("nombre", nombre)
                .claim("rol", rol)
                .setIssuedAt(ahora)
                .setExpiration(new Date(ahora.getTime() + duracionMs))
                .signWith(clave, SignatureAlgorithm.HS256)
                .compact();
    }
}