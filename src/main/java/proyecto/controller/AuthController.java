package proyecto.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.user.UserDTO;
import proyecto.dto.login.LoginRequest;
import proyecto.security.JwtAuthFilter;
import proyecto.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest request, HttpServletResponse resp) {
        var result = authService.login(request.username(), request.password());

        // Cookie HttpOnly con el JWT
        Cookie cookie = new Cookie(JwtAuthFilter.COOKIE_NAME, result.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(12 * 60 * 60);
        cookie.setAttribute("SameSite", "Lax");
        resp.addCookie(cookie);

        return ResponseEntity.ok(result.user());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me() {
        var dto = authService.me();
        return (dto == null) ? ResponseEntity.status(401).build() : ResponseEntity.ok(dto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse resp) {
        Cookie cookie = new Cookie(JwtAuthFilter.COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        resp.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }
}
