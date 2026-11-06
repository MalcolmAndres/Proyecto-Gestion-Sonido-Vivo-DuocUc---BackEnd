package cl.sonidovivo.catalogo.mapper;

import cl.sonidovivo.catalogo.dto.ProductoDTO;
import cl.sonidovivo.catalogo.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    public ProductoDTO toDTO(Producto p, Integer stock) {
        return new ProductoDTO(
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getMarca(),
                p.getModelo(),

                p.getCategoria().getNombre(),
                p.getPrecio(),
                stock,
                p.getDescripcion(),
                p.getImagenUrl(),
                p.getActivo());
    }
}