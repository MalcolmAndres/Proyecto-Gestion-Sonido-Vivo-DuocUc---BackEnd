package cl.sonidovivo.catalogo.service;

import cl.sonidovivo.catalogo.exception.SolicitudInvalidaException;
import cl.sonidovivo.catalogo.model.Categoria;
import cl.sonidovivo.catalogo.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<String> listarNombres() {
        return categoriaRepository.findAllByOrderByNombreAsc().stream()
                .map(Categoria::getNombre)
                .collect(Collectors.toList());
    }

    public String crear(String nombre) {
        String limpio = nombre.trim();
        if (categoriaRepository.existsByNombre(limpio)) {
            throw new SolicitudInvalidaException("La categoría '" + limpio + "' ya existe");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(limpio);
        return categoriaRepository.save(categoria).getNombre();
    }



}