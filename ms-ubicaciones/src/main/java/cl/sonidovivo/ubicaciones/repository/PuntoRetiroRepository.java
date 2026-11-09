package cl.sonidovivo.ubicaciones.repository;

import cl.sonidovivo.ubicaciones.model.PuntoRetiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PuntoRetiroRepository extends JpaRepository<PuntoRetiro, Long> {

    List<PuntoRetiro> findByActivoTrueOrderByIdAsc();
}