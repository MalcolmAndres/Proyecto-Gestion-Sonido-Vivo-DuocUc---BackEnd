package cl.sonidovivo.usuarios.config;

import cl.sonidovivo.usuarios.enums.Rol;
import cl.sonidovivo.usuarios.model.Usuario;
import cl.sonidovivo.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        crear("Dueño Sonido Vivo", "admin@sonidovivo.cl", "admin123", Rol.ADMIN, true);
        crear("Vendedor Uno", "vendedor@sonidovivo.cl", "vendedor123", Rol.VENDEDOR, true);
        crear("Cliente Demo", "cliente@correo.cl", "cliente123", Rol.CLIENTE, true);
        crear("Ex Vendedor", "inactivo@sonidovivo.cl", "inactivo123", Rol.VENDEDOR, false);
        log.info("Usuarios iniciales creados");
    }

    private void crear(String nombre, String email, String clave, Rol rol, boolean activo) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(clave));
        u.setRol(rol);
        u.setActivo(activo);
        usuarioRepository.save(u);
    }
}