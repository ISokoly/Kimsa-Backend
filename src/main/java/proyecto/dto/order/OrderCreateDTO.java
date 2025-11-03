package proyecto.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {
    private Integer idClient;
    private Integer idUser;
    private Integer idTable;
    private Boolean delivery;
    private List<OrderItemDTO> items;
}