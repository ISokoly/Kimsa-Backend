package proyecto.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.dto.order.OrderDetailDTO;
import proyecto.model.Order;
import proyecto.model.OrderDetail;
import proyecto.model.Product;
import proyecto.repo.OrderDetailRepo;
import proyecto.repo.OrderRepo;
import proyecto.repo.ProductRepo;
import proyecto.service.interfaces.InterOrderDetailService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements InterOrderDetailService {
    private final OrderDetailRepo detailRepo;
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    @Override
    public List<OrderDetailDTO> getAll() {
        return detailRepo.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public OrderDetailDTO getById(Integer id) {
        return detailRepo.findById(id).map(this::toDTO).orElse(null);
    }

    @Transactional
    @Override
    public OrderDetailDTO create(OrderDetailDTO dto) {
        Order order = orderRepo.findById(dto.getIdOrder())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        Product product = productRepo.findById(dto.getIdProduct())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        OrderDetail d = new OrderDetail();
        d.setOrder(order);
        d.setProduct(product);
        d.setQuantity(dto.getQuantity());
        BigDecimal sub = BigDecimal.valueOf(product.getPrice())
                .multiply(BigDecimal.valueOf(dto.getQuantity()));
        d.setSubtotal(sub);
        d = detailRepo.save(d);
        order.getDetails().add(d);
        order.recomputeTotal();
        orderRepo.save(order);
        return toDTO(d);
    }

    @Transactional
    @Override
    public OrderDetailDTO update(Integer id, OrderDetailDTO dto) {
        OrderDetail d = detailRepo.findById(id).orElse(null);
        if (d == null) return null;

        if (dto.getIdProduct() != null) {
            Product product = productRepo.findById(dto.getIdProduct())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            d.setProduct(product);
        }
        if (dto.getQuantity() != null) d.setQuantity(dto.getQuantity());

        BigDecimal price = BigDecimal.valueOf(d.getProduct().getPrice());
        d.setSubtotal(price.multiply(BigDecimal.valueOf(d.getQuantity())));
        d = detailRepo.save(d);

        Order order = d.getOrder();
        order.recomputeTotal();
        orderRepo.save(order);

        return toDTO(d);
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        OrderDetail d = detailRepo.findById(id).orElse(null);
        if (d == null) return;
        Order order = d.getOrder();
        detailRepo.delete(d);
        order.recomputeTotal();
        orderRepo.save(order);
    }

    @Override
    public List<OrderDetailDTO> getByOrderId(Integer idOrder) {
        return detailRepo.findByOrder_IdOrder(idOrder).stream().map(this::toDTO).toList();
    }

    private OrderDetailDTO toDTO(OrderDetail d) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setIdDetail(d.getIdDetail());
        dto.setIdOrder(d.getOrder() != null ? d.getOrder().getIdOrder() : null);
        dto.setIdProduct(d.getProduct() != null ? d.getProduct().getIdProduct() : null);
        dto.setQuantity(d.getQuantity());
        dto.setSubtotal(d.getSubtotal());
        return dto;
    }
}