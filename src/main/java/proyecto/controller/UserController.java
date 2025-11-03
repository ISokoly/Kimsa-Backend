package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.PasswordDTO;
import proyecto.dto.user.UserDTO;
import proyecto.dto.user.UserUpdate;
import proyecto.model.User;
import proyecto.repo.UserRepo;
import proyecto.service.interfaces.InterUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor

public class UserController {
    @Autowired
    public final InterUserService service;
    @Autowired
    public final UserRepo repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User savedUser = service.ValidAndSave(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUsers(@PathVariable Integer id, @RequestBody UserUpdate req) {
        User updatedUser = service.updateUser(id, req);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUsersById(@PathVariable Integer id) {
        User user = service.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/actives")
    public List<User> getActivesUsers() {
        return repo.findByDisabledFalse();
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody PasswordDTO dto) {
        try {
            service.changePassword(id, dto.getActual(), dto.getNueva());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/change-password-by-admin")
    public ResponseEntity<Map<String, String>> changePasswordByAdmin(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {

        String nueva = payload.get("nueva");
        service.changePasswordByAdmin(id, nueva);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Contraseña actualizada correctamente ✅");

        return ResponseEntity.ok(response);
    }
}