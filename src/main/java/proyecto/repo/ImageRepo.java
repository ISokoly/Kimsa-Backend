package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.model.Image;

import java.util.Optional;

@Repository
public interface ImageRepo extends JpaRepository<Image, Integer> {
    Optional<Image> findByUrl(String url);
}