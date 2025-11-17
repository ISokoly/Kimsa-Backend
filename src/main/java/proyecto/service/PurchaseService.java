package proyecto.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.dto.purchase.PurchaseCreateDTO;
import proyecto.dto.purchase.PurchaseItemDTO;
import proyecto.dto.purchase.PurchaseUpdateDTO;
import proyecto.model.*;
import proyecto.repo.*;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepo;
    private final SupplierRepository supplierRepo;
    private final SupplyRepository supplyRepo;
    private final PurchaseItemRepository purchaseItemRepo;

    /* ================== CREAR COMPRA ================== */
    @Transactional
    public Purchase create(PurchaseCreateDTO dto) {
        Supplier supplier = supplierRepo.findById(dto.idSupplier())
                .orElseThrow(() -> new IllegalArgumentException("Supplier no encontrado"));
        if (!supplier.isActive()) {
            throw new IllegalStateException("Supplier inactivo");
        }

        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);

        BigDecimal grandTotal = BigDecimal.ZERO;
        List<PurchaseItem> items = new ArrayList<>();

        for (PurchaseItemDTO itemDto : dto.items()) {
            Supply supply = supplyRepo.findById(itemDto.idSupply())
                    .orElseThrow(() -> new IllegalArgumentException("Supply no encontrado: " + itemDto.idSupply()));
            if (!supply.isActive()) {
                throw new IllegalStateException("Supply inactivo: " + supply.getName());
            }

            // Cantidad que el usuario ingresa (kg, L o unidades)
            BigDecimal qtyUser = itemDto.quantity();
            // Cantidad interna para stock (g, ml o unidades)
            BigDecimal qtyBase = qtyUser;

            if (supply.getUnit() == Supply.SupplyUnit.Grams ||
                    supply.getUnit() == Supply.SupplyUnit.Milliliters) {
                qtyBase = qtyUser.multiply(BigDecimal.valueOf(1000)); // stock en g / ml
            }

            BigDecimal unitPrice = supply.getUnitPrice() != null ? supply.getUnitPrice() : BigDecimal.ZERO;

            // Subtotal: precio * cantidad del usuario (NO en gramos/mililitros)
            BigDecimal subtotal = unitPrice.multiply(qtyUser);

            PurchaseItem pi = new PurchaseItem();
            pi.setPurchase(purchase);
            pi.setSupply(supply);
            pi.setQuantity(qtyBase);   // guardamos la cantidad base
            pi.setUnitPrice(unitPrice);
            pi.setSubtotal(subtotal);

            items.add(pi);
            grandTotal = grandTotal.add(subtotal);

            // Actualizar stock del insumo
            BigDecimal curr = supply.getCurrentStock() != null ? supply.getCurrentStock() : BigDecimal.ZERO;
            supply.setCurrentStock(curr.add(qtyBase));
            supplyRepo.save(supply);
        }

        purchase.setTotal(grandTotal);
        purchase.setItems(items);

        Purchase saved = purchaseRepo.save(purchase);
        purchaseItemRepo.saveAll(items);

        return saved;
    }

    /* ================== ACTUALIZAR COMPRA ================== */
    @Transactional
    public Purchase update(Integer id, PurchaseUpdateDTO dto) {
        Purchase existing = purchaseRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));

        Supplier supplier = supplierRepo.findById(dto.idSupplier())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        existing.setSupplier(supplier);

        // Mapa de cantidades antiguas (en unidad base: g/ml/unidades)
        Map<Integer, BigDecimal> oldMap = new HashMap<>();
        for (PurchaseItem pi : existing.getItems()) {
            Integer sid = pi.getSupply().getIdSupply();
            BigDecimal qty = pi.getQuantity() != null ? pi.getQuantity() : BigDecimal.ZERO;
            oldMap.merge(sid, qty, BigDecimal::add);
        }

        List<PurchaseItem> newItems = new ArrayList<>();
        Map<Integer, BigDecimal> newMap = new HashMap<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (PurchaseItemDTO itemDto : dto.items()) {
            Supply supply = supplyRepo.findById(itemDto.idSupply())
                    .orElseThrow(() -> new IllegalArgumentException("Supply no encontrado: " + itemDto.idSupply()));
            if (!supply.isActive()) {
                throw new IllegalStateException("Supply inactivo: " + supply.getName());
            }

            // Cantidad que viene del formulario (kg/L/unidades)
            BigDecimal qtyUser = itemDto.quantity();
            // Cantidad base para stock (g/ml/unidades)
            BigDecimal qtyBase = qtyUser;

            if (supply.getUnit() == Supply.SupplyUnit.Grams ||
                    supply.getUnit() == Supply.SupplyUnit.Milliliters) {
                qtyBase = qtyUser.multiply(BigDecimal.valueOf(1000));
            }

            BigDecimal unitPrice = supply.getUnitPrice() != null ? supply.getUnitPrice() : BigDecimal.ZERO;

            // Subtotal en base a cantidad de usuario
            BigDecimal subtotal = unitPrice.multiply(qtyUser);

            PurchaseItem pi = new PurchaseItem();
            pi.setPurchase(existing);
            pi.setSupply(supply);
            pi.setQuantity(qtyBase);
            pi.setUnitPrice(unitPrice);
            pi.setSubtotal(subtotal);

            newItems.add(pi);
            // Mapa para recalcular stock (en base units)
            newMap.merge(supply.getIdSupply(), qtyBase, BigDecimal::add);

            grandTotal = grandTotal.add(subtotal);
        }

        // Recalcular ajuste de stock por diferencia entre old y new
        Set<Integer> allIds = new HashSet<>();
        allIds.addAll(oldMap.keySet());
        allIds.addAll(newMap.keySet());

        for (Integer sid : allIds) {
            BigDecimal oldQty = oldMap.getOrDefault(sid, BigDecimal.ZERO);
            BigDecimal newQty = newMap.getOrDefault(sid, BigDecimal.ZERO);
            BigDecimal diff = newQty.subtract(oldQty); // >0 sumamos al stock, <0 restamos

            if (diff.compareTo(BigDecimal.ZERO) != 0) {
                Supply supply = supplyRepo.findById(sid)
                        .orElseThrow(() -> new IllegalArgumentException("Supply no encontrado: " + sid));
                BigDecimal curr = supply.getCurrentStock() != null ? supply.getCurrentStock() : BigDecimal.ZERO;
                supply.setCurrentStock(curr.add(diff));
                supplyRepo.save(supply);
            }
        }

        existing.getItems().clear();
        existing.getItems().addAll(newItems);
        existing.setTotal(grandTotal);

        return purchaseRepo.save(existing);
    }
}
