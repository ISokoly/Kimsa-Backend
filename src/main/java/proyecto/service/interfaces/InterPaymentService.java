package proyecto.service.interfaces;

import proyecto.dto.order.PaymentDTO;

import java.util.List;

public interface InterPaymentService {
    List<PaymentDTO> getAll();
    List<PaymentDTO> getByOrderId(Integer idOrder);
    PaymentDTO createSimple(PaymentDTO dto);
    PaymentDTO update(Integer id, PaymentDTO dto);
}