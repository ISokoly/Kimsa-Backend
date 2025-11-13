package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.CategoryResponseDTO;
import proyecto.model.Category;
import proyecto.model.Image;
import proyecto.repo.CategoryRepo;
import proyecto.repo.ImageRepo;
import proyecto.service.interfaces.InterCategoryService;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequestMapping("/api/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final InterCategoryService service;
    private final ImageRepo imageRepo;
    private final CategoryRepo repo;

    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        if (category.getIdImage() != null) {
            Image image = imageRepo.findById(category.getIdImage())
                    .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + category.getIdImage()));
            category.setIdImage(image.getIdImage());
        } else {
            category.setIdImage(null);
        }

        Category savedProduct = service.ValidAndSave(category);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = service.getAllCategories();
        return categories.stream()
                .map(CategoryResponseDTO::new)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        Category existing = service.getCategoryById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setDisabled(category.isDisabled());
        existing.setIdImage(category.getIdImage());

        Category updatedCategory = service.ValidAndSave(existing);
        return ResponseEntity.ok(updatedCategory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) {
        Category category = service.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        Category categoria = service.getCategoryByName(name);
        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<Category> disableCategory(@PathVariable Integer id) {
        try {
            Category updated = service.disableCategoryAndProducts(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<Category> enableCategory(@PathVariable Integer id) {
        try {
            Category updated = service.enableCategoryAndProducts(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}