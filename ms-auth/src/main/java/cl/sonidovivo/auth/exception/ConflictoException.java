package cl.sonidovivo.auth.exception;

// Se responde 409 (ej: correo repetido, sin stock)
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) { super(mensaje); }
}
