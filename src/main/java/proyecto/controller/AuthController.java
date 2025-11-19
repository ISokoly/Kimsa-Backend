package proyecto.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.user.UserDTO;
import proyecto.dto.login.LoginRequest;
import proyecto.dto.login.LoginResponse;
import proyecto.security.JwtAuthFilter;
import proyecto.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse resp) {
        var result = authService.login(request.username(), request.password());

        // Cookie HttpOnly con el JWT (para modo cookies)
        Cookie cookie = new Cookie(JwtAuthFilter.COOKIE_NAME, result.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);              // en producción: true con HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(12 * 60 * 60);
        cookie.setAttribute("SameSite", "Lax");  // si tu frontend está en otro dominio y quieres cookies cross-site: "None"
        resp.addCookie(cookie);

        // También devolvemos token + user en el body (modo header Bearer)
        LoginResponse body = new LoginResponse(result.token(), result.user());
        return ResponseEntity.ok(body);
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
