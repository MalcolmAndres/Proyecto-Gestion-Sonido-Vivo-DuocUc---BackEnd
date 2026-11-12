package cl.sonidovivo.usuarios.service;

import cl.sonidovivo.usuarios.dto.*;
import cl.sonidovivo.usuarios.enums.Rol;
import cl.sonidovivo.usuarios.exception.ConflictoException;
import cl.sonidovivo.usuarios.exception.RecursoNoEncontradoException;
import cl.sonidovivo.usuarios.exception.SolicitudInvalidaException;
import cl.sonidovivo.usuarios.model.Usuario;
import cl.sonidovivo.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAllByOrderByIdAsc().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public UsuarioDTO crear(CrearUsuarioRequest request) {
        return toDTO(guardarNuevo(request.getNombre(), request.getEmail(), request.getPassword(), request.getRol()));
    }

    // Registro público: siempre CLIENTE (nadie puede registrarse como ADMIN)
    @Transactional
    public UsuarioDTO registrar(RegistroRequest request) {
        return toDTO(guardarNuevo(request.getNombre(), request.getEmail(), request.getPassword(), Rol.CLIENTE));
    }

    @Transactional
    public UsuarioDTO actualizar(Long id, ActualizarUsuarioRequest request) {
        Usuario usuario = buscar(id);
        String email = request.getEmail().trim().toLowerCase();
        if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
            throw new ConflictoException("Ese correo ya está registrado");
        }
        usuario.setNombre(request.getNombre().trim());
        usuario.setEmail(email);
        usuario.setRol(request.getRol());
        // Solo cambia la clave si mandaron una nueva
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 6) {
                throw new SolicitudInvalidaException("La contraseña debe tener al menos 6 caracteres");
            }
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO cambiarEstado(Long id, boolean activo, String emailQuienPide) {
        Usuario usuario = buscar(id);
        // Igual que el front: el admin no puede desactivarse a sí mismo
        if (!activo && usuario.getEmail().equals(emailQuienPide)) {
            throw new SolicitudInvalidaException("No puedes desactivar tu propia cuenta");
        }
        usuario.setActivo(activo);
        return toDTO(usuarioRepository.save(usuario));
    }

    // Para ms-auth: incluye el hash de la clave
    public UsuarioInternoDTO buscarInternoPorEmail(String email) {
        Usuario u = usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return new UsuarioInternoDTO(u.getId(), u.getNombre(), u.getEmail(), u.getPassword(),
                u.getRol().name(), u.getActivo());
    }

    private Usuario guardarNuevo(String nombre, String email, String clave, Rol rol) {
        String emailLimpio = email.trim().toLowerCase();
        if (usuarioRepository.existsByEmail(emailLimpio)) {
            throw new ConflictoException("Ese correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setEmail(emailLimpio);
        usuario.setPassword(passwordEncoder.encode(clave));   // se guarda el HASH
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private UsuarioDTO toDTO(Usuario u) {
        return new UsuarioDTO(u.getId(), u.getNombre(), u.getEmail(), u.getRol().name(), u.getActivo());
    }
}