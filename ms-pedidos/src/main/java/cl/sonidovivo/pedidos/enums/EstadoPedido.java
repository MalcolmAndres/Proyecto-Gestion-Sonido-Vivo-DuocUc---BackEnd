package cl.sonidovivo.pedidos.enums;

public enum EstadoPedido {
    PENDIENTE_PAGO,   // pagó con tarjeta: esperando a Webpay
    EN_PREPARACION,
    DESPACHADO,
    ENTREGADO,
    RECHAZADO         // Webpay no aprobó el pago
}