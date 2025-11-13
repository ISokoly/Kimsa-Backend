package proyecto.service.interfaces;

import proyecto.model.Category;

import java.util.List;

public interface InterCategoryService {
    Category ValidAndSave(Category category);
    List<Category> getAllCategories();
    Category getCategoryById(Integer id);
    Category getCategoryByName(String name);

    Category disableCategoryAndProducts(Integer id);
    Category enableCategoryAndProducts(Integer id);
}
