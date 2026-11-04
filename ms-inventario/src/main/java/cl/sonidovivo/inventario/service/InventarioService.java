package cl.sonidovivo.inventario.service;

import cl.sonidovivo.inventario.dto.MovimientoDTO;
import cl.sonidovivo.inventario.dto.StockDTO;
import cl.sonidovivo.inventario.enums.TipoMovimiento;
import cl.sonidovivo.inventario.model.Inventario;
import cl.sonidovivo.inventario.model.MovimientoStock;
import cl.sonidovivo.inventario.repository.InventarioRepository;
import cl.sonidovivo.inventario.repository.MovimientoStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioService {
    private final InventarioRepository inventarioRepository;
    private final MovimientoStockRepository movimientoRepository;

    //GET STOCK
    public List<StockDTO> stockDe(List<Long> productoIds) {
        List<Inventario> encontrados =
                inventarioRepository.findByProductoIdIn(productoIds);
        List<StockDTO> resultado = new ArrayList<>();
        for (Long id : productoIds) {
            int stock = 0;
            for (Inventario inventario : encontrados) {
                if (inventario.getProductoId().equals(id)) {
                    stock = inventario.getStock();

                }

            }
            resultado.add(new StockDTO(id, stock));
        }
        return resultado;
    }
    //UPDATE STOCK
    @Transactional
    public StockDTO actualizarStock(Long productoId, Integer nuevoStock) {
        Inventario inventario =
                inventarioRepository.findByProductoId(productoId).orElse(null);
        if (inventario == null) {
            inventario = new Inventario();
            inventario.setProductoId(productoId);
            inventario.setStock(0);
        }
        int diferencia = nuevoStock - inventario.getStock();
        inventario.setStock(nuevoStock);
        inventarioRepository.save(inventario);
        registrarMovimiento(productoId, diferencia, TipoMovimiento.AJUSTE,
                null);
        log.info("Stock del producto {} ajustado a {} ({})", productoId,
                nuevoStock, diferencia);
        return new StockDTO(productoId, nuevoStock);
    }
    //FUNCIon AUXILIAR QUE REGISTRA MOVIMIENTOS
    public void registrarMovimiento(Long productoId, int cantidad,
                                    TipoMovimiento tipo, Long pedidoId) {
        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setProductoId(productoId);
        movimiento.setCantidad(cantidad);
        movimiento.setTipo(tipo);
        movimiento.setPedidoId(pedidoId);
        movimientoRepository.save(movimiento);
    }
//GET LISTA MOVIMIENTOS
    public List<MovimientoDTO> listarMovimientos(Long productoId) {
        List<MovimientoStock> lista;
        if (productoId == null) {
            lista = movimientoRepository.findAllByOrderByFechaDesc();
        } else {
            lista =
                    movimientoRepository.findByProductoIdOrderByFechaDesc(productoId);
        }
        return lista.stream()
                .map(m -> new MovimientoDTO(m.getId(), m.getProductoId(),
                        m.getCantidad(),
                        m.getTipo().name(), m.getPedidoId(), m.getFecha()))
                .collect(Collectors.toList());
    }
}