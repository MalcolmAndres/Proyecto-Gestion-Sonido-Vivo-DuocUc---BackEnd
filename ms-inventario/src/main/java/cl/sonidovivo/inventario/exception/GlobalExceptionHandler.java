package cl.sonidovivo.inventario.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private ResponseEntity<ErrorResponse> responder(HttpStatus status, String
            mensaje) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(mensaje, status.value(),
                        LocalDateTime.now()));
    }
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse>
    noEncontrado(RecursoNoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorResponse> invalida(SolicitudInvalidaException
                                                          ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> sinStock(StockInsuficienteException
                                                              ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage());
    }
    // Errores de @Valid: junta todos los mensajes en uno
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>
    validacion(MethodArgumentNotValidException ex) {
        String mensajes = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return responder(HttpStatus.BAD_REQUEST, mensajes);
    }
    // Cualquier otro error: 500 sin mostrar detalles internos
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> general(Exception ex) {
        log.error("Error no controlado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado");
    }


}