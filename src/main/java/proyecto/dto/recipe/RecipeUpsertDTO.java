package proyecto.dto.recipe;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record RecipeUpsertDTO(
        @NotNull Integer idProduct,
        @NotEmpty @Valid @JsonAlias({"details","items"}) List<Line> items
) {
    public record Line(
            @NotNull Integer idSupply,
            @NotNull @DecimalMin("0.01") BigDecimal gramsQuantity
    ) {}
}
