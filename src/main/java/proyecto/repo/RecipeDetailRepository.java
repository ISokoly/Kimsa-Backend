package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import proyecto.model.RecipeDetail;

import java.util.List;
import java.util.Optional;

public interface RecipeDetailRepository extends JpaRepository<RecipeDetail, Integer> {

    List<RecipeDetail> findByRecipe_IdRecipe(Integer idRecipe);

    long countByRecipe_IdRecipe(Integer idRecipe);

    @Query("""
           SELECT rd
             FROM RecipeDetail rd
             JOIN FETCH rd.supply s
             JOIN rd.recipe r
             JOIN r.product p
            WHERE p.idProduct = :idProduct
           """)
    List<RecipeDetail> findWithSupplyByProductId(@Param("idProduct") Integer idProduct);

    List<RecipeDetail> findByRecipe_Product_IdProduct(Integer idProduct);

    void deleteByRecipe_IdRecipe(Integer idRecipe);

    Optional<RecipeDetail> findByRecipe_IdRecipeAndSupply_IdSupply(Integer idRecipe, Integer idSupply);
}
