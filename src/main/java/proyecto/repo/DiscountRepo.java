package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.model.Discount;

import java.util.List;

@Repository
public interface DiscountRepo extends JpaRepository<Discount, Integer> {
    List<Discount> findByIdProduct(Integer idProduct);
}
