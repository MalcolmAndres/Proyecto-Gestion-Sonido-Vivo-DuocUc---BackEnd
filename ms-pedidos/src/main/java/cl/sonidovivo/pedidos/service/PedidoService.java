package cl.sonidovivo.pedidos.service;

import cl.sonidovivo.pedidos.client.CatalogoClient;
import cl.sonidovivo.pedidos.client.InventarioClient;
import cl.sonidovivo.pedidos.dto.*;
import cl.sonidovivo.pedidos.enums.EstadoPedido;
import cl.sonidovivo.pedidos.enums.MetodoPago;
import cl.sonidovivo.pedidos.enums.TipoEntrega;
import cl.sonidovivo.pedidos.exception.ConflictoException;
import cl.sonidovivo.pedidos.exception.RecursoNoEncontradoException;
import cl.sonidovivo.pedidos.exception.SolicitudInvalidaException;
import cl.sonidovivo.pedidos.model.DetallePedido;
import cl.sonidovivo.pedidos.model.Pedido;
import cl.sonidovivo.pedidos.repository.PedidoRepository;
import cl.sonidovivo.pedidos.security.UsuarioToken;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CatalogoClient catalogoClient;
    private final InventarioClient inventarioClient;

    // ===== Crear (CLIENTE) =====
    @Transactional
    public PedidoDTO crear(CrearPedidoRequest request) {
        UsuarioToken cliente = UsuarioToken.actual();
        validarEntrega(request);

        // 1) Pedir los productos al catálogo (precio real)
        List<Long> ids = new ArrayList<>();
        for (ItemPedidoRequest item : request.getProductos()) {
            ids.add(item.getId());
        }
        List<ProductoInternoDTO> productosCatalogo = catalogoClient.buscar(ids);

        // 2) Armar el pedido
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(cliente.getId());
        pedido.setClienteNombre(cliente.getNombre());
        pedido.setMetodoPago(request.getMetodoPago());
        pedido.setTipoEntrega(request.getTipoEntrega());
        if (request.getTipoEntrega() == TipoEntrega.DESPACHO) {
            pedido.setDireccion(request.getDireccion().trim());
        } else {
            pedido.setPuntoRetiro(request.getPuntoRetiro());
        }

        if (request.getMetodoPago() == MetodoPago.TARJETA) {
            pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);
        } else {
            pedido.setEstado(EstadoPedido.EN_PREPARACION);
        }

        // 3) Un detalle por producto, con el precio del catálogo
        int total = 0;
        List<ItemReserva> items = new ArrayList<>();
        for (ItemPedidoRequest item : request.getProductos()) {
            ProductoInternoDTO producto = buscarEnLista(productosCatalogo, item.getId());
            if (producto == null || !Boolean.TRUE.equals(producto.getActivo())) {
                throw new SolicitudInvalidaException("Uno de los productos ya no está disponible.");
            }
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(producto.getId());
            detalle.setNombre(producto.getNombre());
            detalle.setPrecio(producto.getPrecio());
            detalle.setCantidad(item.getCantidad());
            pedido.agregarDetalle(detalle);

            total = total + producto.getPrecio() * item.getCantidad();
            items.add(new ItemReserva(producto.getId(), item.getCantidad()));
        }
        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);

        // 4) Apartar el stock
        try {
            inventarioClient.reservar(new ReservaRequest(guardado.getId(), items));
        } catch (FeignException e) {
            throw new ConflictoException("No hay stock suficiente para uno de los productos.");
        }

        // 5) Pago simulado: se descuenta de una vez
        if (guardado.getEstado() == EstadoPedido.EN_PREPARACION) {
            inventarioClient.descontar(new PedidoIdRequest(guardado.getId()));
        }

        log.info("Pedido {} creado para {} por ${}", guardado.getId(), cliente.getEmail(), total);
        return toDTO(guardado);
    }

    // ===== Consultas =====
    @Transactional(readOnly = true)
    public List<PedidoDTO> misPedidos() {
        return pedidoRepository.findByUsuarioIdOrderByFechaDesc(UsuarioToken.actual().getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> todos() {
        return pedidoRepository.findAllByOrderByFechaDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ===== Cambiar estado (VENDEDOR / ADMIN) =====
    @Transactional
    public PedidoDTO cambiarEstado(Long id, EstadoPedido nuevo) {
        Pedido pedido = buscar(id);
        boolean permitido =
                (pedido.getEstado() == EstadoPedido.EN_PREPARACION && nuevo == EstadoPedido.DESPACHADO)
                        || (pedido.getEstado() == EstadoPedido.DESPACHADO && nuevo == EstadoPedido.ENTREGADO);
        if (!permitido) {
            throw new SolicitudInvalidaException("No se permite cambiar a ese estado");
        }
        pedido.setEstado(nuevo);
        return toDTO(pedidoRepository.save(pedido));
    }

    // ===== Reporte (ADMIN) =====
    @Transactional(readOnly = true)
    public ReporteDTO reporte(LocalDate desde, LocalDate hasta) {
        if (desde == null) desde = LocalDate.of(2000, 1, 1);
        if (hasta == null) hasta = LocalDate.now();

        List<EstadoPedido> excluidos = new ArrayList<>();
        excluidos.add(EstadoPedido.PENDIENTE_PAGO);
        excluidos.add(EstadoPedido.RECHAZADO);

        List<Pedido> pedidos = pedidoRepository.findByFechaBetweenAndEstadoNotInOrderByFechaAsc(
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay(), excluidos);

        long total = 0;
        for (Pedido p : pedidos) {
            total = total + p.getTotal();
        }
        List<PedidoDTO> lista = pedidos.stream().map(this::toDTO).collect(Collectors.toList());
        return new ReporteDTO(pedidos.size(), total, lista);
    }

    // ===== Para ms-pagos =====
    public PedidoInternoDTO interno(Long id) {
        Pedido p = buscar(id);
        return new PedidoInternoDTO(p.getId(), p.getUsuarioId(), p.getTotal(), p.getEstado().name());
    }

    @Transactional
    public PedidoDTO registrarPago(Long id, boolean aprobado) {
        Pedido pedido = buscar(id);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE_PAGO) {
            throw new SolicitudInvalidaException("El pedido no está esperando pago");
        }
        if (aprobado) {
            inventarioClient.descontar(new PedidoIdRequest(id));
            pedido.setEstado(EstadoPedido.EN_PREPARACION);
        } else {
            inventarioClient.liberar(new PedidoIdRequest(id));
            pedido.setEstado(EstadoPedido.RECHAZADO);
        }
        return toDTO(pedidoRepository.save(pedido));
    }

    // ===== Ayudantes =====
    private void validarEntrega(CrearPedidoRequest request) {
        if (request.getTipoEntrega() == TipoEntrega.DESPACHO
                && (request.getDireccion() == null || request.getDireccion().isBlank())) {
            throw new SolicitudInvalidaException("Debe ingresar una dirección de despacho.");
        }
        if (request.getTipoEntrega() == TipoEntrega.RETIRO_TIENDA && request.getPuntoRetiro() == null) {
            throw new SolicitudInvalidaException("Debe seleccionar un punto de retiro.");
        }
    }

    private ProductoInternoDTO buscarEnLista(List<ProductoInternoDTO> lista, Long id) {
        for (ProductoInternoDTO p : lista) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));
    }

    private PedidoDTO toDTO(Pedido p) {
        List<DetalleDTO> detalles = new ArrayList<>();
        for (DetallePedido d : p.getProductos()) {
            detalles.add(new DetalleDTO(d.getProductoId(), d.getNombre(), d.getPrecio(), d.getCantidad()));
        }
        return new PedidoDTO(p.getId(), p.getUsuarioId(), p.getClienteNombre(), detalles, p.getTotal(),
                p.getMetodoPago().name(), p.getTipoEntrega().name(),
                p.getDireccion(), p.getPuntoRetiro(),
                p.getFecha(), p.getEstado().name());
    }
}