package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.order.PaymentDTO;
import proyecto.service.interfaces.InterPaymentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final InterPaymentService paymentService;

    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAll();
    }

    @GetMapping("/by-order/{orderId}")
    public List<PaymentDTO> getPaymentsByOrderId(@PathVariable Integer orderId) {
        return paymentService.getByOrderId(orderId);
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Integer id, @RequestBody PaymentDTO dto) {
        PaymentDTO result = paymentService.update(id, dto);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }
}
