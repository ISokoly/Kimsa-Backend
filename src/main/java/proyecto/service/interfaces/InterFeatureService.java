package proyecto.service.interfaces;

import proyecto.model.Feature;
import java.util.List;

public interface InterFeatureService {
    Feature validAndSave(Feature feature);
    List<Feature> getAllFeatures();
    Feature getFeatureById(Integer id);
    Feature updateFeature(Integer id, Feature feature);
    void deleteFeature(Integer id);
}