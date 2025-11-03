package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.ProductResponseDTO;
import proyecto.model.Product;
import proyecto.service.interfaces.InterProductService;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping("/api/products")
@RestController
@RequiredArgsConstructor
public class ProductController {
    public final InterProductService service;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody Product product) {
        Product savedProduct = service.ValidAndSave(product);
        return ResponseEntity.ok(new ProductResponseDTO(savedProduct));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        Product existing = service.getProductById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setDisabled(product.isDisabled());
        existing.setIdImage(product.getIdImage());
        existing.setBrand(product.getBrand());
        existing.setCategory(product.getCategory());
        Product updatedProduct = service.ValidAndSave(existing);
        return ResponseEntity.ok(new ProductResponseDTO(updatedProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        Product product = service.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ProductResponseDTO(product));
    }

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return service.getAllProducts().stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    @GetMapping("/search")
    public List<ProductResponseDTO> searchProductsByName(@RequestParam String name) {
        return service.searchProductsByName(name).stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    @GetMapping("/category/{idCategory}")
    public List<ProductResponseDTO> getProductsByCategory(@PathVariable Integer idCategory) {
        return service.getProductsByCategory(idCategory).stream()
                .map(ProductResponseDTO::new)
                .toList();
    }
}