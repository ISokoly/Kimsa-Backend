package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_features")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProductFeature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProduct", referencedColumnName = "idProduct", nullable = false)
    @JsonIgnoreProperties({"category", "features", "productFeatures", "hibernateLazyInitializer", "handler"})
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idFeature", referencedColumnName = "idFeature", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Feature feature;

    @Column(nullable = false, length = 256)
    private String featureValue;
}