package proyecto.service.interfaces;

import proyecto.dto.order.OrderDetailDTO;

import java.util.List;

public interface InterOrderDetailService {
    List<OrderDetailDTO> getAll();
    OrderDetailDTO getById(Integer id);
    OrderDetailDTO create(OrderDetailDTO dto);
    OrderDetailDTO update(Integer id, OrderDetailDTO dto);
    void delete(Integer id);
    List<OrderDetailDTO> getByOrderId(Integer idOrder);
}