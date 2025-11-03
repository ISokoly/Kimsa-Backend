package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.model.RecipeDetail;

import java.util.List;
import java.util.Optional;

public interface RecipeDetailRepository extends JpaRepository<RecipeDetail, Integer> {

    List<RecipeDetail> findByRecipe_IdRecipe(Integer idRecipe);

    long countByRecipe_IdRecipe(Integer idRecipe);

    void deleteByRecipe_IdRecipe(Integer idRecipe);

    Optional<RecipeDetail> findByRecipe_IdRecipeAndSupply_IdSupply(Integer idRecipe, Integer idSupply);
}
