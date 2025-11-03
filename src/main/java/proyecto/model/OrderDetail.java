package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_details")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrder", nullable = false)
    @JsonIgnoreProperties({"details", "payments", "hibernateLazyInitializer", "handler"})
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProduct", nullable = false)
    @JsonIgnoreProperties({"productFeatures", "category", "brand", "hibernateLazyInitializer", "handler"})
    private Product product;

    private Integer quantity;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;
}
