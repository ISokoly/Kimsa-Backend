package proyecto.service.interfaces;

import proyecto.model.Product;

import java.util.List;

public interface InterProductService {
    Product ValidAndSave(Product product);
    List<Product> getAllProducts();
    Product getProductById(Integer id);
    List<Product> searchProductsByName(String name);
    List<Product> getProductsByCategory(Integer idCategory);
}
