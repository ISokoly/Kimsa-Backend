package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.BrandDTO;
import proyecto.model.Brand;
import proyecto.service.interfaces.InterBrandService;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping("/api/brands")
@RestController
@RequiredArgsConstructor
public class BrandController {
    private final InterBrandService service;

    @PostMapping
    public ResponseEntity<Brand> addBrand(@RequestBody BrandDTO dto) {
        Brand b = new Brand();
        b.setName(dto.name());
        b.setCategory(dto.category());
        Brand saved = service.ValidAndSave(b);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Brand> getAllBrands() {
        return service.getAllBrands();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Integer id) {
        Brand brand = service.getBrandById(id);
        if (brand == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable Integer id, @RequestBody BrandDTO dto) {
        Brand existing = service.getBrandById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setName(dto.name());
        if (dto.category() != null) existing.setCategory(dto.category());

        Brand updated = service.ValidAndSave(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Integer id) {
        Brand existing = service.getBrandById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        service.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}