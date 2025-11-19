package proyecto.dto.login;

import proyecto.dto.user.UserDTO;

public record LoginResponse(
        String token,
        UserDTO user
) {}
