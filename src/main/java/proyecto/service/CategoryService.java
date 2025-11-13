package proyecto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.model.Category;
import proyecto.model.Image;
import proyecto.model.Product;
import proyecto.repo.CategoryRepo;
import proyecto.repo.ImageRepo;
import proyecto.service.interfaces.InterCategoryService;

import java.util.List;

@Service
@AllArgsConstructor
@SuppressWarnings("unused")
public class CategoryService implements InterCategoryService {
    public final CategoryRepo repo;
    public final ImageRepo imageRepo;

    @Override
    public Category ValidAndSave(Category category) {
        if (category.getName() == null || category.getName().isBlank()
                || category.getDescription() == null || category.getDescription().isBlank()) {
            throw new IllegalArgumentException("Nombre y descripción son obligatorios.");
        }

        if (category.getIdImage() != null) {
            Image image = imageRepo.findById(category.getIdImage())
                    .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada"));
            category.setIdImage(image.getIdImage());
        } else {
            category.setIdImage(null);
        }

        return repo.save(category);
    }

    public Category disableCategoryAndProducts(Integer idCategory) {
        Category category = repo.findById(idCategory)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        category.setDisabled(true);

        if (category.getProducts() != null) {
            for (Product product : category.getProducts()) {
                product.setDisabled(true);
            }
        }
        return repo.save(category);
    }

    public Category enableCategoryAndProducts(Integer idCategory) {
        Category category = repo.findById(idCategory)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        category.setDisabled(false);

        if (category.getProducts() != null) {
            for (Product product : category.getProducts()) {
                product.setDisabled(false);
            }
        }
        return repo.save(category);
    }

    public List<Category> getAllCategories() {
        return repo.findAll();
    }

    @Override
    public Category getCategoryById(Integer id) {
        return repo.findById(Math.toIntExact(id)).orElse(null);
    }

    @Override
    public Category getCategoryByName(String name) {
        return repo.findByNameIgnoreCase(name).orElse(null);
    }
}