package cl.sonidovivo.carrito.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// El front lee "mensaje" (v2/src/api/httpClient.js)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String mensaje;
    private int status;
    private LocalDateTime fecha;
}
