package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.model.Supply;

public interface SupplyRepository extends JpaRepository<Supply, Integer> {}
