package cl.bencinaenlinea.finanzas.domain.model;

import cl.bencinaenlinea.bencinera.domain.model.Marca;
import cl.bencinaenlinea.bencinera.domain.model.TipoCombustible;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "descuento",
    indexes = {
        @Index(name = "idx_desc_marca",             columnList = "marca_id"),
        @Index(name = "idx_desc_tarjeta_producto",  columnList = "tarjeta_producto_id"),
        @Index(name = "idx_desc_combustible",       columnList = "tipo_combustible_id"),
        @Index(name = "idx_desc_dia",               columnList = "dia_semana")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("activo = true")
public class Descuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marca_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_desc_marca"))
    private Marca marca;

    /** Null = aplica a cualquier medio de pago. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_producto_id",
                foreignKey = @ForeignKey(name = "fk_desc_tarjeta_producto"))
    private TarjetaProducto tarjetaProducto;

    /** Null = aplica a cualquier combustible. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_combustible_id",
                foreignKey = @ForeignKey(name = "fk_desc_combustible"))
    private TipoCombustible tipoCombustible;

    /** Null = aplica todos los dias. 1 = lunes, 7 = domingo. */
    @Column(name = "dia_semana")
    private Integer diaSemana;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false, length = 20)
    @Builder.Default
    private TipoDescuento tipoDescuento = TipoDescuento.PORCENTAJE;

    @Column(name = "valor_descuento", nullable = false, precision = 8, scale = 2)
    private BigDecimal valorDescuento;

    @Column(name = "tope_maximo", precision = 10, scale = 2)
    private BigDecimal topeMaximo;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
