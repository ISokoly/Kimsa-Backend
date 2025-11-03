package proyecto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.model.Brand;
import proyecto.model.Image;
import proyecto.model.Product;
import proyecto.repo.BrandRepo;
import proyecto.repo.CategoryRepo;
import proyecto.repo.ImageRepo;
import proyecto.repo.ProductRepo;
import proyecto.service.interfaces.InterProductService;

import java.util.List;

@Service
@AllArgsConstructor
@SuppressWarnings("unused")
public class ProductService implements InterProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final BrandRepo brandRepo;
    private final ImageRepo imageRepo;

    @Override
    public Product ValidAndSave(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }

        if (product.getCategory() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }

        boolean existsCategory = categoryRepo.existsById(product.getCategory().getIdCategory());
        if (!existsCategory) {
            throw new IllegalArgumentException("La categoría especificada no existe.");
        }

        if (product.getBrand() != null) {
            Brand brand = brandRepo.findById(product.getBrand().getIdBrand())
                    .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada."));
            product.setBrand(brand);
        } else {
            product.setBrand(null);
        }

        if (product.getIdImage() != null) {
            Image image = imageRepo.findById(product.getIdImage())
                    .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada."));
            product.setIdImage(image.getIdImage());
        } else {
            product.setIdImage(null);
        }

        return productRepo.save(product);
    }


    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepo.findById(id).orElse(null);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepo.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getProductsByCategory(Integer idCategory) {
        return productRepo.findByCategoryIdCategoryAndDisabledFalse(idCategory);
    }
}