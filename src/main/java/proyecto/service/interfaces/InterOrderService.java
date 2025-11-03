package proyecto.service.interfaces;

import proyecto.dto.order.OrderCreateDTO;
import proyecto.dto.order.OrderResponseDTO;
import proyecto.dto.order.OrderUpdateDTO;

import java.util.List;

public interface InterOrderService {
    List<OrderResponseDTO> getAll();
    OrderResponseDTO getById(Integer id);
    OrderResponseDTO create(OrderCreateDTO dto);
    OrderResponseDTO update(Integer id, OrderUpdateDTO dto);
    List<OrderResponseDTO> getConfirmed();
}