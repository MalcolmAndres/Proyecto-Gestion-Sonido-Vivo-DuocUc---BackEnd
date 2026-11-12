package cl.sonidovivo.auth.client;

import cl.sonidovivo.auth.dto.RegistroRequest;
import cl.sonidovivo.auth.dto.UsuarioDTO;
import cl.sonidovivo.auth.dto.UsuarioInternoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "MS-USUARIOS")
public interface UsuariosClient {

    @GetMapping("/internal/usuarios/por-email")
    UsuarioInternoDTO buscarPorEmail(@RequestParam("email") String email);

    @PostMapping("/internal/usuarios")
    UsuarioDTO registrar(@RequestBody RegistroRequest request);
}