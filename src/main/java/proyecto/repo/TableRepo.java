package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.model.RestaurantTable;

import java.util.List;

public interface TableRepo extends JpaRepository<RestaurantTable, Integer> {
    List<RestaurantTable> findByDisabledFalse();
}
