package cl.sonidovivo.usuarios.exception;

// Se responde 404
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
}
