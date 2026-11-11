package cl.sonidovivo.auth.service;

import cl.sonidovivo.auth.client.UsuariosClient;
import cl.sonidovivo.auth.dto.AuthResponse;
import cl.sonidovivo.auth.dto.LoginRequest;
import cl.sonidovivo.auth.dto.UsuarioInternoDTO;
import cl.sonidovivo.auth.exception.CuentaDesactivadaException;
import cl.sonidovivo.auth.exception.SolicitudInvalidaException;
import cl.sonidovivo.auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UsuariosClient usuariosClient;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks AuthService authService;

    private UsuarioInternoDTO cliente(boolean activo) {
        return new UsuarioInternoDTO(3L, "Cliente Demo", "cliente@correo.cl", "$2a$10$hash", "CLIENTE", activo);
    }

    @Test
    void loginCorrectoDevuelveTokenYUsuarioSinClave() {
        when(usuariosClient.buscarPorEmail("cliente@correo.cl")).thenReturn(cliente(true));
        when(passwordEncoder.matches("cliente123", "$2a$10$hash")).thenReturn(true);
        when(jwtService.generar(3L, "Cliente Demo", "cliente@correo.cl", "CLIENTE")).thenReturn("token-falso");

        AuthResponse r = authService.login(new LoginRequest("cliente@correo.cl", "cliente123"));

        assertEquals("token-falso", r.getToken());
        assertEquals("CLIENTE", r.getUsuario().getRol());
    }

    @Test
    void claveIncorrectaLanzaElMismoMensajeQueElMock() {
        when(usuariosClient.buscarPorEmail("cliente@correo.cl")).thenReturn(cliente(true));
        when(passwordEncoder.matches("mala", "$2a$10$hash")).thenReturn(false);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class, () ->
                authService.login(new LoginRequest("cliente@correo.cl", "mala")));
        assertEquals("Correo o contraseña incorrectos", ex.getMessage());
    }

    @Test
    void usuarioDesactivadoNoPuedeEntrar() {
        when(usuariosClient.buscarPorEmail("cliente@correo.cl")).thenReturn(cliente(false));
        when(passwordEncoder.matches("cliente123", "$2a$10$hash")).thenReturn(true);

        assertThrows(CuentaDesactivadaException.class, () ->
                authService.login(new LoginRequest("cliente@correo.cl", "cliente123")));
        verify(jwtService, never()).generar(any(), any(), any(), any());
    }
}