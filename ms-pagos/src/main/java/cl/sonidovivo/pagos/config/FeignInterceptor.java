package cl.sonidovivo.pagos.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// Reenvía el token del usuario en cada llamada Feign (el mismo de tu proyecto TCG)
@Component
public class FeignInterceptor implements RequestInterceptor {

    // Clave compartida para las rutas /internal/** de los otros microservicios
    @Value("${internal.key}")
    private String claveInterna;

    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Internal-Key", claveInterna);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null) {
                template.header("Authorization", authorizationHeader);
            }
        }
    }
}
