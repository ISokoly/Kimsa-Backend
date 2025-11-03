package proyecto.dto;

import lombok.Data;
import proyecto.model.Category;

@Data
public class CategoryResponseDTO {
    private Integer idCategory;
    private String name;
    private String description;
    private boolean disabled;
    private Integer idImage;

    public CategoryResponseDTO(Category category) {
        this.idCategory = category.getIdCategory();
        this.name = category.getName();
        this.description = category.getDescription();
        this.disabled = category.isDisabled();
        this.idImage = category.getIdImage() != null ? category.getIdImage(): null;
    }
}
