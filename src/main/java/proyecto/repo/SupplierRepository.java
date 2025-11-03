package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {}
