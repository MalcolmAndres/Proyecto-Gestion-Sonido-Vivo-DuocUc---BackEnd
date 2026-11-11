package cl.sonidovivo.usuarios.service;

import cl.sonidovivo.usuarios.dto.CrearUsuarioRequest;
import cl.sonidovivo.usuarios.dto.UsuarioDTO;
import cl.sonidovivo.usuarios.enums.Rol;
import cl.sonidovivo.usuarios.exception.ConflictoException;
import cl.sonidovivo.usuarios.exception.SolicitudInvalidaException;
import cl.sonidovivo.usuarios.model.Usuario;
import cl.sonidovivo.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UsuarioRepository usuarioRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UsuarioService usuarioService;

    @Test
    void crearGuardaLaClaveHasheadaYNoLaDevuelve() {
        when(usuarioRepository.existsByEmail("ana@correo.cl")).thenReturn(false);
        when(passwordEncoder.encode("secreta1")).thenReturn("$2a$10$hashFalso");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO dto = usuarioService.crear(new CrearUsuarioRequest("Ana", "ana@correo.cl", "secreta1", Rol.VENDEDOR));

        verify(usuarioRepository).save(argThat(u -> u.getPassword().equals("$2a$10$hashFalso")));
        assertEquals("VENDEDOR", dto.getRol());
    }

    @Test
    void noSePuedeCrearUnCorreoRepetido() {
        when(usuarioRepository.existsByEmail("admin@sonidovivo.cl")).thenReturn(true);

        assertThrows(ConflictoException.class, () ->
                usuarioService.crear(new CrearUsuarioRequest("Copia", "admin@sonidovivo.cl", "123456", Rol.ADMIN)));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void elAdminNoPuedeDesactivarseASiMismo() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setEmail("admin@sonidovivo.cl");
        admin.setRol(Rol.ADMIN);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(SolicitudInvalidaException.class, () ->
                usuarioService.cambiarEstado(1L, false, "admin@sonidovivo.cl"));
    }
}