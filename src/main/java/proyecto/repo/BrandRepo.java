package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.model.Brand;

@Repository
public interface BrandRepo extends JpaRepository<Brand, Integer> {
}