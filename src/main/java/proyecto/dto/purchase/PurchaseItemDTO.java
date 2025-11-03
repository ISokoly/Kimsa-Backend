package proyecto.dto.purchase;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PurchaseItemDTO(
        @NotNull Integer idSupply,
        @NotNull @DecimalMin("0.01") BigDecimal quantity
) {}
