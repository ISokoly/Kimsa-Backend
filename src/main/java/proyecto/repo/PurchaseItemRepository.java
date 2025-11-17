package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.model.PurchaseItem;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Integer> {
}
