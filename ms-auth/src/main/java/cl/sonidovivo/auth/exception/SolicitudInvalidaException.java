package cl.sonidovivo.auth.exception;

// Se responde 400
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) { super(mensaje); }
}
