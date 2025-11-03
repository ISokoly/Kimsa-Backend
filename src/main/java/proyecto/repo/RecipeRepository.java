package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.model.Recipe;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    @Query("select r from Recipe r left join fetch r.product")
    List<Recipe> findAllWithProduct();
}
