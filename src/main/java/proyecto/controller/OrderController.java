package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.order.OrderCreateDTO;
import proyecto.dto.order.OrderResponseDTO;
import proyecto.dto.order.OrderUpdateDTO;
import proyecto.service.interfaces.InterOrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final InterOrderService orderService;

    @GetMapping("/sales")
    public List<OrderResponseDTO> getAllSales() {
        return orderService.getAll();
    }

    @GetMapping("/sales/{id}")
    public ResponseEntity<OrderResponseDTO> getSaleById(@PathVariable Integer id) {
        OrderResponseDTO result = orderService.getById(id);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @PostMapping("/sales")
    public ResponseEntity<OrderResponseDTO> createSale(@RequestBody OrderCreateDTO dto) {
        return ResponseEntity.ok(orderService.create(dto));
    }

    @PutMapping("/sales/{id}")
    public ResponseEntity<OrderResponseDTO> updateSale(@PathVariable Integer id, @RequestBody OrderUpdateDTO dto) {
        OrderResponseDTO result = orderService.update(id, dto);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/confirmed")
    public List<OrderResponseDTO> getConfirmedSales() {
        return orderService.getConfirmed();
    }
}