package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import proyecto.model.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findByStatus(Order.OrderStatus status);

    @Query("""
           SELECT o
             FROM Order o
             LEFT JOIN FETCH o.details d
             LEFT JOIN FETCH d.product p
            WHERE o.idOrder = :id
           """)
    Optional<Order> findByIdWithDetails(Integer id);

}