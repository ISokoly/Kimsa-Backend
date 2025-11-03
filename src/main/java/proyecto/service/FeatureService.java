package proyecto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.model.Feature;
import proyecto.repo.FeatureRepo;
import proyecto.service.interfaces.InterFeatureService;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FeatureService implements InterFeatureService {
    private final FeatureRepo featureRepo;

    @Override
    public Feature validAndSave(Feature feature) {
        return featureRepo.save(feature);
    }

    @Override
    public List<Feature> getAllFeatures() {
        return featureRepo.findAll();
    }

    @Override
    public Feature getFeatureById(Integer id) {
        return featureRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Feature no encontrada con id: " + id));
    }

    @Override
    public Feature updateFeature(Integer id, Feature updatedFeature) {
        Feature existing = getFeatureById(id);
        if (updatedFeature.getFeatureName() == null || updatedFeature.getFeatureName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la característica no puede estar vacío");
        }

        existing.setFeatureName(updatedFeature.getFeatureName());

        return featureRepo.save(existing);
    }

    @Override
    public void deleteFeature(Integer id) {
        Feature existing = getFeatureById(id);
        featureRepo.delete(existing);
    }
}