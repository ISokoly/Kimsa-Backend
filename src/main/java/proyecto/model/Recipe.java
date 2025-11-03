package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Recipes")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRecipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProduct", nullable = false, referencedColumnName = "idProduct")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Product product;
}
