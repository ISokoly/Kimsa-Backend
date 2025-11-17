// src/main/java/proyecto/controller/InventoryController.java
package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.service.InventoryService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/consume/{orderId}")
    public ResponseEntity<InventoryService.AdjustmentSummary> consume(
            @PathVariable Integer orderId,
            @RequestParam(defaultValue = "false") boolean forbidNegative
    ) {
        return ResponseEntity.ok(inventoryService.consumeForOrderId(orderId, forbidNegative));
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<InventoryService.AdjustmentSummary> refund(@PathVariable Integer orderId) {
        return ResponseEntity.ok(inventoryService.refundForOrderId(orderId));
    }
}
