package proyecto.dto.purchase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PurchaseUpdateDTO(
        @NotNull Integer idSupplier,
        @NotEmpty List<@Valid PurchaseItemDTO> items
) {}
