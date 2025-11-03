package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "RecipeDetails",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idRecipe", "idSupply"})
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RecipeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRecipeDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRecipe", nullable = false, referencedColumnName = "idRecipe")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idSupply", nullable = false, referencedColumnName = "idSupply")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Supply supply;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal gramsQuantity;
}
