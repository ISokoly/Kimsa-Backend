package proyecto.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    Page<Purchase> findBySupplier_IdSupplier(Integer idSupplier, Pageable pageable);
}
