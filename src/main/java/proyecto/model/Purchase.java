package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "Purchases")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPurchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSupplier", nullable = false, referencedColumnName = "idSupplier")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSupply", nullable = false, referencedColumnName = "idSupply")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Supply supply;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull @DecimalMin("0.01")
    private BigDecimal quantity;

    @Column(name = "purchaseDate")
    private LocalDateTime purchaseDate = LocalDateTime.now();

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull @DecimalMin("0.00")
    private BigDecimal total;
}
