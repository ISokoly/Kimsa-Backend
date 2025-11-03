package proyecto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.model.Client;
import proyecto.repo.ClientRepo;
import proyecto.service.interfaces.InterClientService;

import java.util.List;

@Service
@AllArgsConstructor
@SuppressWarnings("unused")
public class ClientService implements InterClientService {

    private final ClientRepo repo;

    @Override
    public Client ValidAndSave(Client client) {
        if (client == null || client.getName() == null || client.getName().isBlank() ||
                client.getDni() == null || client.getDni().isBlank()) {
            throw new IllegalArgumentException("Nombre y DNI son obligatorios.");
        }
        return repo.save(client);
    }

    public List<Client> getAllClients() {
        return repo.findAll();
    }

    @Override
    public Client getClientById(Integer id) {
        return repo.findById(Math.toIntExact(id)).orElse(null);
    }

    @Override
    public Client getClientByDni(String dni) {
        return repo.findByDni(dni).orElse(null);
    }

    @Override
    public List<Client> searchClientsByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }
}