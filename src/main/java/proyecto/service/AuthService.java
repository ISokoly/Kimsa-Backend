// src/main/java/proyecto/service/AuthService.java
package proyecto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.mapper.UserMapper;
import proyecto.dto.user.UserDTO;
import proyecto.model.User;
import proyecto.repo.UserRepo;
import proyecto.security.JwtService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;

    public LoginResult login(String username, String rawPassword) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña inválidos"));

        if (u.isDisabled()) {
            throw new RuntimeException("Usuario deshabilitado");
        }
        if (!passwordEncoder.matches(rawPassword, u.getPassword())) {
            throw new RuntimeException("Usuario o contraseña inválidos");
        }

        String role = (u.getRol() == null) ? "Employee" : u.getRol().name();
        String token = jwt.generate(u.getUsername(), Map.of("uid", u.getIdUser(), "role", role));
        return new LoginResult(UserMapper.toDTO(u), token);
    }

    public UserDTO me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        String username = String.valueOf(auth.getPrincipal());
        return userRepo.findByUsername(username).map(UserMapper::toDTO).orElse(null);
    }

    public record LoginResult(UserDTO user, String token) {}
}
