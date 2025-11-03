package proyecto.service.interfaces;

import proyecto.model.ProductFeature;
import java.util.List;

public interface InterProductFeatureService {
    ProductFeature validAndSave(ProductFeature productFeature);
    List<ProductFeature> getAllProductFeatures();
    ProductFeature getProductFeatureById(Integer id);
    List<ProductFeature> getFeaturesByProduct(Integer idProduct);
    ProductFeature updateProductFeature(Integer id, ProductFeature productFeature);
    void deleteProductFeature(Integer id);
}