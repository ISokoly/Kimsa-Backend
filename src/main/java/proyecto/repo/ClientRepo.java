package proyecto.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.model.Client;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepo extends JpaRepository<Client, Integer> {
    List<Client> findByNameContainingIgnoreCase(String name);
    Optional<Client> findByDni(String dni);
}