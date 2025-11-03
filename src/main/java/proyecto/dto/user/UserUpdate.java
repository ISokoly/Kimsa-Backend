package proyecto.dto.user;

import lombok.Data;
import proyecto.model.User;

@Data
public class UserUpdate {
    private String name;
    private String lastName;
    private String username;
    private String dni;
    private String direction;
    private String numberPhone;
    private User.Rol rol;
    private Boolean disabled;
    private Boolean administratorPermissions;
}
