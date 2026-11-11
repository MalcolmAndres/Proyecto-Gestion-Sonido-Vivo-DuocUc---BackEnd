package cl.sonidovivo.pagos.model;

import cl.sonidovivo.pagos.enums.EstadoPago;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private Integer monto;

    @Column(name = "buy_order", nullable = false, unique = true, length = 26)
    private String buyOrder;

    @Column(name = "session_id", nullable = false, length = 61)
    private String sessionId;

    @Column(unique = true, length = 100)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;

    @Column(name = "codigo_autorizacion", length = 20)
    private String codigoAutorizacion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void alCrear() {
        fecha = LocalDateTime.now();
    }
}