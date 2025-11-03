package proyecto.dto.order;

import lombok.Data;
import proyecto.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Integer idPayment;
    private Integer idOrder;
    private BigDecimal amount;
    private Payment.PaymentType paymentType;
    private LocalDateTime paymentDate;
}