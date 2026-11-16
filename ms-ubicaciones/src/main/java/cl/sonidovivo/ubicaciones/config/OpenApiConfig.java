package cl.sonidovivo.ubicaciones.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger/OpenAPI: registra el esquema JWT Bearer para que
 * aparezca el boton "Authorize" en Swagger UI y el token se envie en todas las peticiones.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "MS-UBICACIONES API", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Pega aqui el token JWT obtenido en /api/auth/login (sin el prefijo 'Bearer')",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
