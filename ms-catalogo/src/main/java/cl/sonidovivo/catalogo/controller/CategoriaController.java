package cl.sonidovivo.catalogo.controller;
import cl.sonidovivo.catalogo.dto.CategoriaRequest;
import cl.sonidovivo.catalogo.service.CategoriaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping
    public List<String> listar() {
        return categoriaService.listarNombres();
    }

    @PostMapping
    public ResponseEntity<String> clear(@Valid @RequestBody CategoriaRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(request.getNombre()));
    }
}