package proyecto.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.dto.order.PaymentDTO;
import proyecto.model.Order;
import proyecto.model.Payment;
import proyecto.repo.OrderRepo;
import proyecto.repo.PaymentRepo;
import proyecto.service.interfaces.InterPaymentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService implements InterPaymentService {

    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;

    @Override
    public List<PaymentDTO> getAll() {
        return paymentRepo.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public List<PaymentDTO> getByOrderId(Integer idOrder) {
        return paymentRepo.findByOrder_IdOrder(idOrder).stream().map(this::toDTO).toList();
    }

    @Transactional
    public PaymentDTO createSimple(PaymentDTO dto) {
        Order order = orderRepo.findById(dto.getIdOrder())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        Payment p = new Payment();
        p.setOrder(order);
        p.setAmount(dto.getAmount());
        p.setPaymentType(dto.getPaymentType());
        p.setPaymentDate(dto.getPaymentDate());
        return toDTO(paymentRepo.save(p));
    }

    @Transactional
    @Override
    public PaymentDTO update(Integer id, PaymentDTO dto) {
        Payment p = paymentRepo.findById(id).orElse(null);
        if (p == null) return null;
        if (dto.getAmount() != null) p.setAmount(dto.getAmount());
        if (dto.getPaymentType() != null) p.setPaymentType(dto.getPaymentType());
        if (dto.getPaymentDate() != null) p.setPaymentDate(dto.getPaymentDate());
        return toDTO(paymentRepo.save(p));
    }

    private PaymentDTO toDTO(Payment p) {
        PaymentDTO d = new PaymentDTO();
        d.setIdPayment(p.getIdPayment());
        d.setIdOrder(p.getOrder() != null ? p.getOrder().getIdOrder() : null);
        d.setAmount(p.getAmount());
        d.setPaymentType(p.getPaymentType());
        d.setPaymentDate(p.getPaymentDate());
        return d;
    }
}
