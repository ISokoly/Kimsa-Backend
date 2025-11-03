package proyecto.dto.order;

import lombok.Data;
import proyecto.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Integer idOrder;
    private Integer idClient;
    private Integer idUser;
    private Integer idTable;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private Order.OrderStatus status;
    private BigDecimal total;
    private Boolean delivery;
    private List<OrderDetailDTO> details;
}