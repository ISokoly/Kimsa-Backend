package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.model.OrderDetail;

import java.util.List;

@Repository
public interface OrderDetailRepo extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrder_IdOrder(Integer idOrder);
}