package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.ClientEnsureDTO;
import proyecto.model.Client;
import proyecto.service.interfaces.InterClientService;

import java.util.List;

@RequestMapping("/api/clients")
@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ClientController {
    public final InterClientService service;

    @PostMapping
    public ResponseEntity<Client> addClient(@RequestBody Client client) {
        Client savedClient = service.ValidAndSave(client);
        return ResponseEntity.ok(savedClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Integer id, @RequestBody Client client) {
        Client updatedClient = service.ValidAndSave(client);

        if (updatedClient == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedClient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Integer id) {
        Client client = service.getClientById(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(client);
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Client> getClientByDni(@PathVariable String dni) {
        Client client = service.getClientByDni(dni);
        if (client == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(client);
    }

    @PostMapping("/ensure")
    public ResponseEntity<Client> ensureByDni(@RequestBody ClientEnsureDTO dto) {
        if (dto.getDni() == null || dto.getDni().isBlank()
                || dto.getName() == null || dto.getName().isBlank()
                || dto.getBirthdate() == null) {
            return ResponseEntity.badRequest().build();
        }

        Client existing = service.getClientByDni(dto.getDni());
        if (existing != null) {
            return ResponseEntity.ok(existing);
        }

        Client c = new Client();
        c.setName(dto.getName().trim());
        c.setDni(dto.getDni().trim());
        c.setBirthdate(dto.getBirthdate());
        return ResponseEntity.ok(service.ValidAndSave(c));
    }

    @GetMapping
    public List<Client> getAllClients() {
        return service.getAllClients();
    }

    @GetMapping("/search")
    public List<Client> searchClientsByName(@RequestParam String name) {
        return service.searchClientsByName(name);
    }
}