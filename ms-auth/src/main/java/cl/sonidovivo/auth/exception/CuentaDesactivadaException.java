package cl.sonidovivo.auth.exception;

// Se responde 403 (cuenta desactivada por el admin)
public class CuentaDesactivadaException extends RuntimeException {
    public CuentaDesactivadaException(String mensaje) { super(mensaje); }
}
