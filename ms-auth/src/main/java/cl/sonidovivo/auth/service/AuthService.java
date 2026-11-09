package cl.sonidovivo.auth.service;

import cl.sonidovivo.auth.client.UsuariosClient;
import cl.sonidovivo.auth.dto.*;
import cl.sonidovivo.auth.exception.ConflictoException;
import cl.sonidovivo.auth.exception.CuentaDesactivadaException;
import cl.sonidovivo.auth.exception.SolicitudInvalidaException;
import cl.sonidovivo.auth.security.JwtService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    // El mismo mensaje del mockApi. Es igual si falla el correo o la clave:
    // así no se revela qué correos existen.
    private static final String CREDENCIALES = "Correo o contraseña incorrectos";

    private final UsuariosClient usuariosClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        UsuarioInternoDTO usuario;
        try {
            usuario = usuariosClient.buscarPorEmail(request.getEmail().trim().toLowerCase());
        } catch (FeignException.NotFound e) {        // ms-usuarios respondió 404: ese correo no existe
            throw new SolicitudInvalidaException(CREDENCIALES);
        }

        // BCrypt compara la clave escrita con el hash guardado
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new SolicitudInvalidaException(CREDENCIALES);
        }
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new CuentaDesactivadaException("Tu cuenta está desactivada");
        }

        log.info("Login correcto: {}", usuario.getEmail());
        return respuesta(new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(),
                usuario.getRol(), usuario.getActivo()));
    }

    public AuthResponse registro(RegistroRequest request) {
        try {
            UsuarioDTO creado = usuariosClient.registrar(request);
            return respuesta(creado);
        } catch (FeignException.Conflict e) {        // ms-usuarios respondió 409
            throw new ConflictoException("Ese correo ya está registrado");
        }
    }

    private AuthResponse respuesta(UsuarioDTO usuario) {
        String token = jwtService.generar(usuario.getId(),
                usuario.getNombre(), usuario.getEmail(), usuario.getRol());
        return new AuthResponse(token, usuario);
    }
}