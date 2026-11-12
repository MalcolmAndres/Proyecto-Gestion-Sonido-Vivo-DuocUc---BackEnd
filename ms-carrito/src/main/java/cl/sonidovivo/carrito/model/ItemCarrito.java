package cl.sonidovivo.carrito.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// Una fila = un producto en el carrito de un cliente.
// Solo ids y cantidad: nombre, precio y stock se piden a ms-catalogo al mostrarlos.
@Data
@NoArgsConstructor
@Entity
@Table(name = "item_carrito",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "producto_id"}))
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Integer cantidad;
}