package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.order.OrderDetailDTO;
import proyecto.service.interfaces.InterOrderDetailService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order-details")
public class OrderDetailController {

    private final InterOrderDetailService orderDetailService;

    @GetMapping("/sales")
    public List<OrderDetailDTO> getAllOrderDetails() {
        return orderDetailService.getAll();
    }

    @GetMapping("/sales/{id}")
    public ResponseEntity<OrderDetailDTO> getOrderDetailById(@PathVariable("id") Integer id) {
        OrderDetailDTO result = orderDetailService.getById(id);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @PostMapping("/sales")
    public ResponseEntity<OrderDetailDTO> createOrderDetail(@RequestBody OrderDetailDTO dto) {
        return ResponseEntity.ok(orderDetailService.create(dto));
    }

    @PutMapping("/sales/{id}")
    public ResponseEntity<OrderDetailDTO> updateOrderDetail(@PathVariable("id") Integer id,
                                                            @RequestBody OrderDetailDTO dto) {
        OrderDetailDTO result = orderDetailService.update(id, dto);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @DeleteMapping("/sales/{id}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable("id") Integer id) {
        orderDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-order/{orderId}")
    public List<OrderDetailDTO> getOrderDetailsByOrderId(@PathVariable Integer orderId) {
        return orderDetailService.getByOrderId(orderId);
    }
}
