package cl.sonidovivo.pagos.webpay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WebpayClient {

    private static final String TRANSACCIONES = "/rswebpaytransaction/api/webpay/v1.2/transactions";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${webpay.url}")
    private String url;

    @Value("${webpay.codigo-comercio}")
    private String codigoComercio;

    @Value("${webpay.api-key}")
    private String apiKey;

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Tbk-Api-Key-Id", codigoComercio);
        headers.set("Tbk-Api-Key-Secret", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // Paso 1: crear la transacción -> devuelve token + url
    public WebpayCrearResponse crear(WebpayCrearRequest request) {
        HttpEntity<WebpayCrearRequest> peticion = new HttpEntity<>(request, headers());
        return restTemplate.postForObject(url + TRANSACCIONES, peticion, WebpayCrearResponse.class);
    }

    // Paso 2: confirmar (commit) cuando el cliente vuelve
    public WebpayCommitResponse confirmar(String token) {
        HttpEntity<Void> peticion = new HttpEntity<>(headers());
        return restTemplate.exchange(url + TRANSACCIONES + "/" + token, HttpMethod.PUT,
                peticion, WebpayCommitResponse.class).getBody();
    }
}