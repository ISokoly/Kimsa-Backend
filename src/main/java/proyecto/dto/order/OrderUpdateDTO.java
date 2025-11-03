package proyecto.dto.order;

import lombok.Data;
import proyecto.model.Order;

import java.util.List;

@Data
public class OrderUpdateDTO {
    private Integer idClient;
    private Integer idUser;
    private Integer idTable;
    private Boolean delivery;
    private Order.OrderStatus status;

    private List<OrderItemDTO> items;
}
