package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.order.PaymentDTO;
import proyecto.service.PaymentService; // ← lo usamos directamente, no la interfaz

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAll();
    }

    @GetMapping("/by-order/{orderId}")
    public List<PaymentDTO> getPaymentsByOrderId(@PathVariable Integer orderId) {
        return paymentService.getByOrderId(orderId);
    }

    // --- pago simple (no confirma ni toca inventario)
    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.createSimple(dto));
    }

    // --- flujo completo: registrar pago + confirmar pedido + consumir insumos
    @PostMapping("/confirm")
    public ResponseEntity<PaymentDTO> confirmAndPay(@RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.createSimple(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Integer id, @RequestBody PaymentDTO dto) {
        PaymentDTO result = paymentService.update(id, dto);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }
}
