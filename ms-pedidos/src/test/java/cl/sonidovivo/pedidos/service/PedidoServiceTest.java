package cl.sonidovivo.pedidos.service;

import cl.sonidovivo.pedidos.client.CatalogoClient;
import cl.sonidovivo.pedidos.client.InventarioClient;
import cl.sonidovivo.pedidos.dto.PedidoDTO;
import cl.sonidovivo.pedidos.dto.PedidoIdRequest;
import cl.sonidovivo.pedidos.enums.EstadoPedido;
import cl.sonidovivo.pedidos.enums.MetodoPago;
import cl.sonidovivo.pedidos.enums.TipoEntrega;
import cl.sonidovivo.pedidos.exception.SolicitudInvalidaException;
import cl.sonidovivo.pedidos.model.Pedido;
import cl.sonidovivo.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock PedidoRepository pedidoRepository;
    @Mock CatalogoClient catalogoClient;
    @Mock