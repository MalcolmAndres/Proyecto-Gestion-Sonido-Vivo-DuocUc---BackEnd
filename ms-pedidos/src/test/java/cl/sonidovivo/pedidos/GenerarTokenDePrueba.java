package cl.sonidovivo.pedidos;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

// Genera un token de prueba (8 horas) para Postman mientras ms-auth no exista.
// Run -> Edit Configurations -> Environment variables:
//   JWT_SECRET=<la de tu .env>;ROL=ADMIN      (o VENDEDOR, o CLIENTE)
public class GenerarTokenDePrueba {

    public static void main(String[] args) {
        String secreto = System.getenv("JWT_SECRET");
        String rol = System.getenv().getOrDefault("ROL", "ADMIN");
        if (secreto == null || secreto.isBlank()) {
            System.out.println("Falta la variable de entorno JWT_SECRET");
            return;
        }
        // Los mismos usuarios de data de prueba (ids 1, 2 y 3)
        long id = rol.equals("ADMIN") ? 1 : rol.equals("VENDEDOR") ? 2 : 3;
        String nombre = rol.equals("ADMIN") ? "Dueño Sonido Vivo" : rol.equals("VENDEDOR") ? "Vendedor Uno" : "Cliente Demo";
        String email = rol.equals("ADMIN") ? "admin@sonidovivo.cl" : rol.equals("VENDEDOR") ? "vendedor@sonidovivo.cl" : "cliente@correo.cl";

        Key clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
        String token = Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .claim("id", id)
                .claim("nombre", nombre)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 8))
                .signWith(clave, SignatureAlgorithm.HS256)
                .compact();
        System.out.println(token);
    }
}
