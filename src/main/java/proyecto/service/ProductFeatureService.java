package proyecto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.model.ProductFeature;
import proyecto.repo.ProductFeatureRepo;
import proyecto.service.interfaces.InterProductFeatureService;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ProductFeatureService implements InterProductFeatureService {

    private final ProductFeatureRepo productFeatureRepository;

    @Override
    public ProductFeature validAndSave(ProductFeature productFeature) {
        if (productFeature.getFeatureValue() == null || productFeature.getFeatureValue().isBlank()) {
            throw new IllegalArgumentException("El valor de la característica no puede estar vacío");
        }
        return productFeatureRepository.save(productFeature);
    }

    @Override
    public List<ProductFeature> getAllProductFeatures() {
        return productFeatureRepository.findAll();
    }

    @Override
    public ProductFeature getProductFeatureById(Integer id) {
        return productFeatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductFeature no encontrada con id: " + id));
    }

    @Override
    public List<ProductFeature> getFeaturesByProduct(Integer idProduct) {
        return productFeatureRepository.findByProductIdProduct(idProduct);
    }

    @Override
    public ProductFeature updateProductFeature(Integer id, ProductFeature updatedProductFeature) {
        ProductFeature existing = getProductFeatureById(id);

        if (updatedProductFeature.getFeatureValue() == null || updatedProductFeature.getFeatureValue().isBlank()) {
            throw new IllegalArgumentException("El valor de la característica no puede estar vacío");
        }

        existing.setFeatureValue(updatedProductFeature.getFeatureValue());
        existing.setFeature(updatedProductFeature.getFeature());
        existing.setProduct(updatedProductFeature.getProduct());

        return productFeatureRepository.save(existing);
    }

    @Override
    public void deleteProductFeature(Integer id) {
        ProductFeature existing = getProductFeatureById(id);
        productFeatureRepository.delete(existing);
    }
}