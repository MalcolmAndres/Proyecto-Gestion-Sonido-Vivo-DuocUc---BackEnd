package cl.sonidovivo.catalogo.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignInterceptor implements RequestInterceptor {

    // Clave compartida para las rutas /internal/** de los otros microservicios
    @Value("${internal.key}")
    private String claveInterna;

    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Internal-Key", claveInterna);

        ServletRequestAttributes atributos =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (atributos == null) {
            return;
        }

        HttpServletRequest request = atributos.getRequest();
        String token = request.getHeader("Authorization");

        if (token != null) {
            template.header("Authorization", token);
        }
    }
}