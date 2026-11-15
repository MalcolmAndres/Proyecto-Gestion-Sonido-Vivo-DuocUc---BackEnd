package cl.sonidovivo.auth.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// ms-auth llama a /internal/usuarios de ms-usuarios: envía la clave compartida
@Component
public class FeignInterceptor implements RequestInterceptor {

    @Value("${internal.key}")
    private String claveInterna;

    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Internal-Key", claveInterna);
    }
}