// proyecto/dto/OrderDetailDTO.java
package proyecto.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailDTO {
    private Integer idDetail;
    private Integer idOrder;
    private Integer idProduct;
    private Integer quantity;
    private BigDecimal subtotal;
}
