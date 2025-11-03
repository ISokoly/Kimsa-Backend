package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBrand;

    @Column(nullable = false)
    private Integer category;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"brand", "category", "productFeatures"})
    private List<Product> products;
}