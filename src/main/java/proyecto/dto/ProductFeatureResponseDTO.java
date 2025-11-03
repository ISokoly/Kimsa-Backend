package proyecto.dto;

import lombok.Data;
import proyecto.model.ProductFeature;

@Data
public class ProductFeatureResponseDTO {
    private Integer idProductFeature;
    private Integer idProduct;
    private Integer idFeature;
    private String featureName;
    private String featureValue;

    public ProductFeatureResponseDTO(ProductFeature pf) {
        this.idProductFeature = pf.getIdProductFeature();
        this.idProduct = pf.getProduct() != null ? pf.getProduct().getIdProduct() : null;
        this.idFeature = pf.getFeature() != null ? pf.getFeature().getIdFeature() : null;
        this.featureName = pf.getFeature() != null ? pf.getFeature().getFeatureName() : null;
        this.featureValue = pf.getFeatureValue();
    }
}