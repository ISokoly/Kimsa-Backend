package proyecto.mapper;

import proyecto.dto.user.UserDTO;
import proyecto.model.User;

public class UserMapper {
    public static UserDTO toDTO(User u) {
        if (u == null) return null;
        return new UserDTO(
                u.getIdUser(),
                u.getUsername(),
                u.getName(),
                u.getLastName(),
                u.getDni(),
                u.getDirection(),
                u.getNumberPhone(),
                u.getRol() == null ? null : u.getRol().name(),
                u.isDisabled(),
                u.isAdministratorPermissions()
        );
    }
}