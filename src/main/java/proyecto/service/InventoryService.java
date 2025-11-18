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

    public record AdjustmentItem(Integer idSupply, String name,
                                 BigDecimal before, BigDecimal delta, BigDecimal after) {
    }

    public record AdjustmentSummary(Integer idOrder, List<AdjustmentItem> items) {
    }

    // ⭐ NUEVO: DTO simple para exponer stock por insumo asociado a la orden
    public record SupplyLowStockDTO(
            Integer idSupply,
            String name,
            String unit,
            BigDecimal currentStock
    ) {
    }

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

    /**
     * Consume stock de insumos por todos los detalles del pedido.
     */
    @Transactional
    public AdjustmentSummary consumeForOrder(Order order, boolean forbidNegative) {
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

                BigDecimal consumo = porProducto.multiply(qtyProd).setScale(SCALE, RoundingMode.HALF_UP);
                BigDecimal before = s.getCurrentStock() == null ? BigDecimal.ZERO : s.getCurrentStock();
                BigDecimal after = before.subtract(consumo).setScale(SCALE, RoundingMode.HALF_UP);

                if (forbidNegative && after.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalStateException("Stock insuficiente para insumo '" + s.getName()
                            + "' (id=" + s.getIdSupply() + "). Requerido: " + consumo + ", Disponible: " + before);
                }
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

    @Transactional
    public List<SupplyLowStockDTO> getLowStockByOrder(Integer idOrder) {
        Order order = orderRepo.findById(idOrder)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + idOrder));

        if (order.getDetails() == null || order.getDetails().isEmpty()) {
            return List.of();
        }

        // Usamos un map para no repetir el mismo insumo varias veces
        Map<Integer, Supply> suppliesMap = new LinkedHashMap<>();

        for (OrderDetail d : order.getDetails()) {
            if (d.getProduct() == null) continue;

            Integer idProduct = d.getProduct().getIdProduct();
            List<RecipeDetail> receta = recipeDetailRepo.findWithSupplyByProductId(idProduct);

            for (RecipeDetail rd : receta) {
                Supply s = rd.getSupply();
                if (s == null || s.getIdSupply() == null) continue;
                suppliesMap.putIfAbsent(s.getIdSupply(), s);
            }
        }

        List<SupplyLowStockDTO> result = new ArrayList<>();
        for (Supply s : suppliesMap.values()) {
            String unit = s.getUnit() != null ? String.valueOf(s.getUnit()) : "";
            BigDecimal stock = s.getCurrentStock() != null ? s.getCurrentStock() : BigDecimal.ZERO;

            result.add(new SupplyLowStockDTO(
                    s.getIdSupply(),
                    s.getName(),
                    unit,
                    stock
            ));
        }

        return result;
    }
}