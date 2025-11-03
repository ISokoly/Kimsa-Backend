package proyecto.dto;

import lombok.Data;
import proyecto.model.Feature;

@Data
public class FeatureResponseDTO {
    private Integer idFeature;
    private String featureName;

    public FeatureResponseDTO(Feature feature) {
        this.idFeature = feature.getIdFeature();
        this.featureName = feature.getFeatureName();
    }
}