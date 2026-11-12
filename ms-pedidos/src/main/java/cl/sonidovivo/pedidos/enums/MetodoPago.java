package cl.sonidovivo.pedidos.enums;

public enum MetodoPago {
    TRANSFERENCIA,  // simulado: el pedido queda pagado al tiro
    TARJETA,        // real (ambiente de pruebas): pasa por Webpay
    EFECTIVO        // simulado: paga al retirar
}