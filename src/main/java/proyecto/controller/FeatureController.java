package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.FeatureResponseDTO;
import proyecto.model.Feature;
import proyecto.service.interfaces.InterFeatureService;

import java.util.List;

@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FeatureController {
    private final InterFeatureService featureService;

    @PostMapping
    public ResponseEntity<FeatureResponseDTO> createFeature(@RequestBody Feature feature) {
        Feature saved = featureService.validAndSave(feature);
        return ResponseEntity.ok(new FeatureResponseDTO(saved));
    }

    @GetMapping
    public List<FeatureResponseDTO> getAllFeatures() {
        return featureService.getAllFeatures()
                .stream()
                .map(FeatureResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureResponseDTO> getFeatureById(@PathVariable Integer id) {
        Feature feature = featureService.getFeatureById(id);
        return ResponseEntity.ok(new FeatureResponseDTO(feature));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeatureResponseDTO> updateFeature(@PathVariable Integer id, @RequestBody Feature feature) {
        Feature updated = featureService.updateFeature(id, feature);
        return ResponseEntity.ok(new FeatureResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeature(@PathVariable Integer id) {
        featureService.deleteFeature(id);
        return ResponseEntity.noContent().build();
    }
}