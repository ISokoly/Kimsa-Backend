package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.model.ProductFeature;
import proyecto.service.interfaces.InterProductFeatureService;

import java.util.List;

@RestController
@RequestMapping("/api/product-features")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ProductFeatureController {
    private final InterProductFeatureService productFeatureService;

    @PostMapping
    public ResponseEntity<ProductFeature> createProductFeature(@RequestBody ProductFeature pf) {
        return ResponseEntity.ok(productFeatureService.validAndSave(pf));
    }

    @GetMapping
    public List<ProductFeature> getAllProductFeatures() {
        return productFeatureService.getAllProductFeatures();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductFeature> getProductFeatureById(@PathVariable Integer id) {
        ProductFeature pf = productFeatureService.getProductFeatureById(id);
        return ResponseEntity.ok(pf);
    }

    @GetMapping("/product/{idProduct}")
    public List<ProductFeature> getFeaturesByProduct(@PathVariable Integer idProduct) {
        return productFeatureService.getFeaturesByProduct(idProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductFeature> updateProductFeature(@PathVariable Integer id, @RequestBody ProductFeature pf) {
        ProductFeature updated = productFeatureService.updateProductFeature(id, pf);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductFeature(@PathVariable Integer id) {
        productFeatureService.deleteProductFeature(id);
        return ResponseEntity.noContent().build();
    }
}