package cl.sonidovivo.ubicaciones.service;

import cl.sonidovivo.ubicaciones.dto.PuntoRetiroDTO;
import cl.sonidovivo.ubicaciones.dto.PuntoRetiroRequest;
import cl.sonidovivo.ubicaciones.exception.RecursoNoEncontradoException;
import cl.sonidovivo.ubicaciones.model.PuntoRetiro;
import cl.sonidovivo.ubicaciones.repository.PuntoRetiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PuntoRetiroService {

    private final PuntoRetiroRepository repository;

    public List<PuntoRetiroDTO> listar() {
        return repository.findByActivoTrueOrderByIdAsc().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PuntoRetiroDTO obtener(Long id) {
        return toDTO(buscar(id));
    }

    @Transactional
    public PuntoRetiroDTO crear(PuntoRetiroRequest request) {
        PuntoRetiro punto = new PuntoRetiro();
        aplicar(punto, request);
        return toDTO(repository.save(punto));
    }

    @Transactional
    public PuntoRetiroDTO actualizar(Long id, PuntoRetiroRequest request) {
        PuntoRetiro punto = buscar(id);
        aplicar(punto, request);
        return toDTO(repository.save(punto));
    }

    @Transactional
    public void desactivar(Long id) {
        PuntoRetiro punto = buscar(id);
        punto.setActivo(false);
        repository.save(punto);
    }

    private void aplicar(PuntoRetiro punto, PuntoRetiroRequest request) {
        punto.setNombre(request.getNombre());
        punto.setDireccion(request.getDireccion());
        punto.setLatitud(request.getLatitud());
        punto.setLongitud(request.getLongitud());
    }

    private PuntoRetiro buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Punto de retiro no encontrado"));
    }

    private PuntoRetiroDTO toDTO(PuntoRetiro p) {
        return new PuntoRetiroDTO(p.getId(), p.getNombre(), p.getDireccion(), p.getLatitud(), p.getLongitud());
    }
}