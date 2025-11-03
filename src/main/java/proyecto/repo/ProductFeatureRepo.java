package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.model.ProductFeature;

import java.util.List;

@Repository
public interface ProductFeatureRepo extends JpaRepository<ProductFeature, Integer> {
    List<ProductFeature> findByProductIdProduct(Integer idProduct);
}