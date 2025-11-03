package proyecto.dto;

import lombok.Data;
import proyecto.model.Product;

@Data
public class ProductResponseDTO {
    private Integer idProduct;
    private String name;
    private Double price;
    private Integer idCategory;
    private Integer idBrand;
    private boolean disabled;
    private Integer idImage;

    public ProductResponseDTO(Product product) {
        this.idProduct = product.getIdProduct();
        this.name = product.getName();
        this.price = product.getPrice();
        this.idCategory = product.getCategory() != null ? product.getCategory().getIdCategory() : null;
        this.idBrand = product.getBrand() != null ? product.getBrand().getIdBrand() : null;
        this.disabled = product.isDisabled();
        this.idImage = product.getIdImage() != null ? product.getIdImage() : null;
    }
}