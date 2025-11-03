package proyecto.service.interfaces;

import proyecto.model.Client;

import java.util.List;

public interface InterClientService {
    Client ValidAndSave(Client product);
    List<Client> getAllClients();
    Client getClientById(Integer id);
    Client getClientByDni(String dni);
    List<Client> searchClientsByName(String name);
}
