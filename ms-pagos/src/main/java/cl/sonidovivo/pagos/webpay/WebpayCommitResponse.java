package cl.sonidovivo.pagos.webpay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebpayCommitResponse {
    @JsonProperty("buy_order")
    private String buyOrder;

    private Integer amount;
    private String status;

    @JsonProperty("authorization_code")
    private String authorizationCode;

    @JsonProperty("response_code")
    private Integer responseCode;

    public boolean estaAprobado() {
        return responseCode != null && responseCode == 0 && "AUTHORIZED".equals(status);
    }
}