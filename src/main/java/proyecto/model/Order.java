package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idClient")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUser")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTable")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RestaurantTable table;

    private LocalDate orderDate;
    private LocalTime orderTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.Pending;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    private boolean delivery;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"order", "product"})
    private List<OrderDetail> details = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"order"})
    private List<Payment> payments = new ArrayList<>();

    public void recomputeTotal() {
        this.total = details.stream()
                .map(od -> od.getSubtotal() == null ? BigDecimal.ZERO : od.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public enum OrderStatus {
        Pending, Confirmed, Cancelled
    }
}
