package proyecto.dto.user;

public record UserDTO(
        Integer idUser,
        String username,
        String name,
        String lastName,
        String dni,
        String direction,
        String numberPhone,
        String rol,
        boolean disabled,
        boolean administratorPermissions
) {}
