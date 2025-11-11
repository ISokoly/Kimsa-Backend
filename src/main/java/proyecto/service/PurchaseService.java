package proyecto.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.dto.purchase.PurchaseCreateDTO;
import proyecto.dto.purchase.PurchaseItemDTO;
import proyecto.model.Purchase;
import proyecto.model.Supplier;
import proyecto.model.Supply;
import proyecto.repo.PurchaseRepository;
import proyecto.repo.SupplierRepository;
import proyecto.repo.SupplyRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepo;
    private final SupplierRepository supplierRepo;
    private final SupplyRepository supplyRepo;

    @Transactional
    public List<Purchase> createMany(PurchaseCreateDTO dto) {
        Supplier supplier = supplierRepo.findById(dto.idSupplier())
                .orElseThrow(() -> new IllegalArgumentException("Supplier no encontrado"));
        if (!supplier.isActive()) throw new IllegalStateException("Supplier inactivo");

        List<Purchase> created = new ArrayList<>();

        for (PurchaseItemDTO item : dto.items()) {
            Supply supply = supplyRepo.findById(item.idSupply())
                    .orElseThrow(() -> new IllegalArgumentException("Supply no encontrado: " + item.idSupply()));
            if (!supply.isActive()) throw new IllegalStateException("Supply inactivo: " + supply.getName());

            BigDecimal quantity = item.quantity();
            if (supply.getUnit() == Supply.SupplyUnit.Grams || supply.getUnit() == Supply.SupplyUnit.Milliliters) {
                quantity = quantity.multiply(BigDecimal.valueOf(1000));
            }

            BigDecimal unit = supply.getUnitPrice() != null ? supply.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal total = unit.multiply(quantity);

            Purchase p = new Purchase();
            p.setSupplier(supplier);
            p.setSupply(supply);
            p.setQuantity(quantity);
            p.setTotal(total);

            Purchase saved = purchaseRepo.save(p);
            created.add(saved);

            BigDecimal curr = supply.getCurrentStock() != null ? supply.getCurrentStock() : BigDecimal.ZERO;
            supply.setCurrentStock(curr.add(quantity));
            supplyRepo.save(supply);
        }

        return created;
    }
}