package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.model.Feature;

import java.util.List;

@Repository
public interface FeatureRepo extends JpaRepository<Feature, Integer> {
}