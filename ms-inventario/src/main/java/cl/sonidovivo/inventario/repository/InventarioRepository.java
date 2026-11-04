package cl.sonidovivo.inventario.repository;

import cl.sonidovivo.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface InventarioRepository  extends JpaRepository<Inventario,Long>{
Optional<Inventario> findByProductoId(Long productoId);

List<Inventario> findByProductoIdIn(List<Long> prdocutoIds);
}