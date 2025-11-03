package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrder", nullable = false)
    @JsonIgnoreProperties({"details", "payments", "hibernateLazyInitializer", "handler"})
    private Order order;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private LocalDateTime paymentDate = LocalDateTime.now();

    public enum PaymentType {
        Cash, Card, Transfer
    }
}