package proyecto.dto.recipe;

import jakarta.validation.constraints.*;

import java.util.List;

public record RecipeCreateDTO(
        @NotNull Integer idProduct,
        @NotNull List<Detail> details
) {
    public record Detail(@NotNull Integer idSupply,
                         @NotNull @DecimalMin("0.01") Double gramsQuantity) {
    }
}
