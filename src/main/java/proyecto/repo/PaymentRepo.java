package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.model.Payment;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    List<Payment> findByOrder_IdOrder(Integer idOrder);
}