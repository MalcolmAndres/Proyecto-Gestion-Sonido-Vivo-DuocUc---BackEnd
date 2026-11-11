package cl.sonidovivo.inventario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

// Genera un token ADMIN de prueba (8 horas) para usar en Postman mientras ms-auth no exista.
// Antes de correrlo: Run -> Edit Configurations -> Environment variables -> JWT_SECRET=<la de tu .env>
// Para un token de VENDEDOR o CLIENTE, cambia "rol" (y "id"/"nombre" si quieres).
public class GenerarTokenDePrueba {

    public static void main(String[] args) {
        String secreto = System.getenv("JWT_SECRET");
        if (secreto == null || secreto.isBlank()) {
            System.out.println("Falta la variable de entorno JWT_SECRET");
            return;
        }
        Key clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));

        String token = Jwts.builder()
                .setSubject("admin@sonidovivo.cl")
                .claim("rol", "ADMIN")
                .claim("id", 1)
                .claim("nombre", "Dueño Sonido Vivo")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 8))
                .signWith(clave, SignatureAlgorithm.HS256)
                .compact();

        System.out.println(token);
    }
}
