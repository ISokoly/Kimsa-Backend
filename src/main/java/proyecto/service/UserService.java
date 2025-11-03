package proyecto.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.dto.user.UserUpdate;
import proyecto.model.User;
import proyecto.repo.UserRepo;
import proyecto.service.interfaces.InterUserService;

import java.util.List;

@Service
@AllArgsConstructor
@SuppressWarnings("unused")
public class UserService implements InterUserService {

    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;

    public User login(String username, String password) {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.isDisabled()) {
            throw new RuntimeException("Usuario deshabilitado");
        }
        return user;
    }

    @Override
    public User ValidAndSave(User user) {
        if (user == null || user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Nombre es obligatorio.");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        if (repo.existsByDni(user.getDni())) {
            throw new IllegalArgumentException("El DNI ya está registrado.");
        }

        if (user.getRol() == User.Rol.Administrator) {
            user.setAdministratorPermissions(true);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repo.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        return repo.findById(Math.toIntExact(id)).orElse(null);
    }

    @Override
    public User updateUser(Integer id, UserUpdate user) {
        User existing = repo.findById(id).orElse(null);
        if (existing == null) return null;

        if (nonEmpty(user.getName()))       existing.setName(user.getName());
        if (user.getLastName() != null)     existing.setLastName(user.getLastName());
        if (user.getUsername() != null)     existing.setUsername(user.getUsername());

        if (user.getDni() != null && !user.getDni().equals(existing.getDni())) {
            if (repo.existsByDni(user.getDni())) {
                throw new IllegalArgumentException("El DNI ya está registrado por otro usuario");
            }
            existing.setDni(user.getDni());
        }

        if (user.getDirection() != null)    existing.setDirection(user.getDirection());
        if (user.getNumberPhone() != null)  existing.setNumberPhone(user.getNumberPhone());

        if (user.getRol() != null) {
            existing.setRol(user.getRol());

            if (user.getRol() == User.Rol.Administrator) {
                existing.setAdministratorPermissions(true);
            } else {
                if (user.getAdministratorPermissions() != null) {
                    existing.setAdministratorPermissions(user.getAdministratorPermissions());
                }
            }
        } else {
            if (user.getAdministratorPermissions() != null) {
                existing.setAdministratorPermissions(user.getAdministratorPermissions());
            }
        }

        if (user.getDisabled() != null && user.getDisabled() != existing.isDisabled()) {
            existing.setDisabled(user.getDisabled());
        }

        return repo.save(existing);
    }

    private boolean nonEmpty(String s) { return s != null && !s.isBlank(); }

    public void encryptPasswordsExistingUsers() {
        List<User> usuarios = repo.findAll();
        for (User u : usuarios) {
            if (!u.getPassword().startsWith("$2a$")) {
                u.setPassword(passwordEncoder.encode(u.getPassword()));
                repo.save(u);
            }
        }
    }

    public void changePassword(Integer userId, String actual, String nueva) {
        User user = repo.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(actual, user.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(nueva));
        repo.save(user);
    }


    public void changePasswordByAdmin(Integer id, String nueva) {
        User user = repo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(nueva));
        repo.save(user);
    }
}
