package cl.sonidovivo.inventario.repository;

import cl.sonidovivo.inventario.model.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoStockRepository  extends JpaRepository<MovimientoStock,Long>{
    List<MovimientoStock> findAllByOrderByFechaDesc();
    List<MovimientoStock> findByProductoIdOrderByFechaDesc(Long productoId);
}