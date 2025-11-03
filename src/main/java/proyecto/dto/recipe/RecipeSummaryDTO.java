package proyecto.dto.recipe;

public record RecipeSummaryDTO(
        Integer idRecipe,
        Integer idProduct,
        String productName,
        long detailsCount
) {}
