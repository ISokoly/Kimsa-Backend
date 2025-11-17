package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "PurchaseItems")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPurchaseItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPurchase", nullable = false, referencedColumnName = "idPurchase")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSupply", nullable = false, referencedColumnName = "idSupply")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Supply supply;

    // cantidad en la UNIDAD BASE que maneja stock (unidades, gramos, ml, etc.)
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull @DecimalMin("0.01")
    private BigDecimal quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull @DecimalMin("0.00")
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull @DecimalMin("0.00")
    private BigDecimal subtotal;
}
