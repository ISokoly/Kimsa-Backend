package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "Supplies")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Supply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSupply;

    @Column(nullable = false, unique = true, length = 150)
    @NotBlank
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull @DecimalMin("0.00")
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull @DecimalMin("0.00")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @NotNull
    private SupplyUnit unit = SupplyUnit.Units;

    public enum SupplyUnit {
        Grams,
        Milliliters,
        Units
    }
}
