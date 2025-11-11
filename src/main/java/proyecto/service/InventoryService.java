// src/main/java/proyecto/service/InventoryService.java
package proyecto.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import proyecto.model.Order;
import proyecto.model.OrderDetail;
import proyecto.model.RecipeDetail;
import proyecto.model.Supply;
import proyecto.repo.OrderRepo;
import proyecto.repo.RecipeDetailRepository;
import proyecto.repo.SupplyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final OrderRepo orderRepo;
    private final RecipeDetailRepository recipeDetailRepo;
    private final SupplyRepository supplyRepo;

    private static final int SCALE = 2;

    public Object checkItems(List<Map<String, Integer>> items, int safetyPercent) {
        Map<Integer, BigDecimal> consumoPorSupply = new HashMap<>();
        Map<Integer, String> supplyName = new HashMap<>();
        Map<Integer, String> supplyUnit = new HashMap<>();

        if (items == null || items.isEmpty()) {
            return Map.of("insufficient", List.of(), "lowStock", List.of(), "bySupply", List.of());
        }

        // 1️⃣ Acumular consumo por insumo desde recetas
        for (Map<String, Integer> it : items) {
            if (it == null) continue;
            Integer idProduct = it.get("idProduct");
            Integer qty = it.getOrDefault("quantity", 1);
            if (idProduct == null || qty == null || qty <= 0) continue;

            List<RecipeDetail> receta = recipeDetailRepo.findWithSupplyByProductId(idProduct);
            if (receta == null || receta.isEmpty()) continue;

            BigDecimal qProd = BigDecimal.valueOf(qty);
            for (RecipeDetail rd : receta) {
                if (rd == null || rd.getSupply() == null) continue;

                Supply s = rd.getSupply();
                Integer idS = s.getIdSupply();
                BigDecimal porProducto = rd.getGramsQuantity() != null ? rd.getGramsQuantity() : BigDecimal.ZERO;
                BigDecimal consumo = porProducto.multiply(qProd).setScale(SCALE, RoundingMode.HALF_UP);

                consumoPorSupply.merge(idS, consumo, BigDecimal::add);
                supplyName.putIfAbsent(idS, s.getName());
                supplyUnit.putIfAbsent(idS, s.getUnit() != null ? s.getUnit().name() : "Units");
            }
        }

        // 2️⃣ Comparar consumo con stock actual
        List<Map<String, Object>> insufficient = new ArrayList<>();
        List<Map<String, Object>> lowStock = new ArrayList<>();
        List<Map<String, Object>> bySupply = new ArrayList<>();

        for (var e : consumoPorSupply.entrySet()) {
            Integer idSupply = e.getKey();
            BigDecimal need = e.getValue();

            Supply s = supplyRepo.findById(idSupply).orElse(null);
            if (s == null) continue;

            BigDecimal current = s.getCurrentStock() != null ? s.getCurrentStock() : BigDecimal.ZERO;
            BigDecimal remaining = current.subtract(need).setScale(SCALE, RoundingMode.HALF_UP);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("idSupply", idSupply);
            row.put("name", supplyName.get(idSupply));
            row.put("unit", supplyUnit.get(idSupply));
            row.put("currentStock", current);
            row.put("need", need);
            row.put("remaining", remaining);
            bySupply.add(row);

            if (remaining.compareTo(BigDecimal.ZERO) < 0) {
                insufficient.add(Map.of(
                        "idSupply", idSupply,
                        "supplyName", supplyName.get(idSupply),
                        "unit", supplyUnit.get(idSupply),
                        "currentStock", current,
                        "needed", need,
                        "missing", remaining.abs()
                ));
            } else {
                BigDecimal threshold = current
                        .multiply(BigDecimal.valueOf(safetyPercent))
                        .divide(BigDecimal.valueOf(100), SCALE, RoundingMode.HALF_UP);

                if (remaining.compareTo(threshold) <= 0) {
                    lowStock.add(Map.of(
                            "idSupply", idSupply,
                            "supplyName", supplyName.get(idSupply),
                            "unit", supplyUnit.get(idSupply),
                            "remaining", remaining,
                            "threshold", threshold
                    ));
                }
            }
        }

        return Map.of(
                "insufficient", insufficient,
                "lowStock", lowStock,
                "bySupply", bySupply
        );
    }

    public record AdjustmentItem(Integer idSupply, String name,
                                 BigDecimal before, BigDecimal delta, BigDecimal after) {}

    public record AdjustmentSummary(Integer idOrder, List<AdjustmentItem> items) {}

    @Transactional
    public AdjustmentSummary consumeForOrderId(Integer idOrder, boolean forbidNegative) {
        Order order = orderRepo.findById(idOrder)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + idOrder));
        return consumeForOrder(order, forbidNegative);
    }

    @Transactional
    public AdjustmentSummary refundForOrderId(Integer idOrder) {
        Order order = orderRepo.findById(idOrder)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + idOrder));
        return refundForOrder(order);
    }

    /** Consume stock de insumos por todos los detalles del pedido. */
    @Transactional
    public AdjustmentSummary consumeForOrder(Order order, boolean forbidNegative) {
        if (order == null || order.getDetails() == null || order.getDetails().isEmpty()) {
            return new AdjustmentSummary(order != null ? order.getIdOrder() : null, List.of());
        }

        log.info("[INV] Consumo para pedido {} con {} detalle(s)", order.getIdOrder(), order.getDetails().size());
        Map<Integer, AdjustmentItem> changes = new LinkedHashMap<>();

        for (OrderDetail d : order.getDetails()) {
            if (d.getProduct() == null || d.getQuantity() == null) continue;

            Integer idProduct = d.getProduct().getIdProduct();
            List<RecipeDetail> receta = recipeDetailRepo.findWithSupplyByProductId(idProduct);
            log.info("[INV] Producto {} (detalle {}): {} receta(s) encontradas",
                    idProduct, d.getIdDetail(), receta.size());

            BigDecimal qtyProd = BigDecimal.valueOf(d.getQuantity());

            for (RecipeDetail rd : receta) {
                Supply s = rd.getSupply();
                if (s == null) continue;

                BigDecimal porProducto = rd.getGramsQuantity() == null
                        ? BigDecimal.ZERO : rd.getGramsQuantity();

                BigDecimal consumo = porProducto.multiply(qtyProd).setScale(SCALE, RoundingMode.HALF_UP);
                BigDecimal before = s.getCurrentStock() == null ? BigDecimal.ZERO : s.getCurrentStock();
                BigDecimal after = before.subtract(consumo).setScale(SCALE, RoundingMode.HALF_UP);

                if (forbidNegative && after.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalStateException("Stock insuficiente para insumo '" + s.getName()
                            + "' (id=" + s.getIdSupply() + "). Requerido: " + consumo + ", Disponible: " + before);
                }

                log.info("[INV] Supply {}: stock {} - consumo {} = {}", s.getIdSupply(), before, consumo, after);
                s.setCurrentStock(after);
                supplyRepo.save(s);

                changes.put(s.getIdSupply(), new AdjustmentItem(
                        s.getIdSupply(), s.getName(), before, consumo.negate(), after
                ));
            }
        }
        return new AdjustmentSummary(order.getIdOrder(), new ArrayList<>(changes.values()));
    }

    @Transactional
    public AdjustmentSummary refundForOrder(Order order) {
        if (order == null || order.getDetails() == null || order.getDetails().isEmpty()) {
            return new AdjustmentSummary(order != null ? order.getIdOrder() : null, List.of());
        }

        Map<Integer, AdjustmentItem> changes = new LinkedHashMap<>();

        for (OrderDetail d : order.getDetails()) {
            if (d.getProduct() == null || d.getQuantity() == null) continue;

            Integer idProduct = d.getProduct().getIdProduct();
            List<RecipeDetail> receta = recipeDetailRepo.findWithSupplyByProductId(idProduct);

            BigDecimal qtyProd = BigDecimal.valueOf(d.getQuantity());

            for (RecipeDetail rd : receta) {
                Supply s = rd.getSupply();
                if (s == null) continue;

                BigDecimal porProducto = rd.getGramsQuantity() == null
                        ? BigDecimal.ZERO : rd.getGramsQuantity();

                BigDecimal add = porProducto.multiply(qtyProd).setScale(SCALE, RoundingMode.HALF_UP);
                BigDecimal before = s.getCurrentStock() == null ? BigDecimal.ZERO : s.getCurrentStock();
                BigDecimal after = before.add(add).setScale(SCALE, RoundingMode.HALF_UP);

                s.setCurrentStock(after);
                supplyRepo.save(s);

                changes.put(s.getIdSupply(), new AdjustmentItem(
                        s.getIdSupply(), s.getName(), before, add, after
                ));
            }
        }
        return new AdjustmentSummary(order.getIdOrder(), new ArrayList<>(changes.values()));
    }
}
