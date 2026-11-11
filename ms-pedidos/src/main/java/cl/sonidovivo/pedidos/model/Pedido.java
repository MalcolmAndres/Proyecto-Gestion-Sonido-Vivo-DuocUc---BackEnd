package cl.sonidovivo.pedidos.model;

import cl.sonidovivo.pedidos.enums.EstadoPedido;
import cl.sonidovivo.pedidos.enums.MetodoPago;
import cl.sonidovivo.pedidos.enums.TipoEntrega;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "cliente_nombre", nullable = false)
    private String clienteNombre;

    @Column(nullable = false)
    private Integer total;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrega", nullable = false, length = 20)
    private TipoEntrega tipoEntrega;

    private String direccion;

    @Column(name = "punto_retiro")
    private Long puntoRetiro;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> productos = new ArrayList<>();

    @PrePersist
    protected void alCrear() {
        if (fecha == null) fecha = LocalDateTime.now();
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        productos.add(detalle);
    }
}