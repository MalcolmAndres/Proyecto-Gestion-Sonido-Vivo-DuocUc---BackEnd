package cl.sonidovivo.catalogo.repository;

import cl.sonidovivo.catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto,Long>{

    @Query("""
SELECT p FROM Producto p
WHERE p.activo = true
AND (:categoria IS NULL OR p.categoria.nombre = :categoria)
AND (:texto IS NULL
OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
OR LOWER(p.marca)  LIKE LOWER(CONCAT('%', :texto, '%')))
ORDER BY p.id
      """)
    List<Producto> buscar(@Param("categoria") String categoria,
                          @Param("texto") String texto);
    boolean existsByCodigo(String codigo);
    List<Producto> findByIdIn(List<Long> ids);
}