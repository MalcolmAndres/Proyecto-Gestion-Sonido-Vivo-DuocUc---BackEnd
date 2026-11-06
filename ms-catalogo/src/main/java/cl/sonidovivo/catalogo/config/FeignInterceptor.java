package cl.sonidovivo.catalogo.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
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