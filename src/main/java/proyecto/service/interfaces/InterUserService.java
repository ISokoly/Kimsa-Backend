package proyecto.service.interfaces;

import proyecto.dto.user.UserUpdate;
import proyecto.model.User;

import java.util.List;

public interface InterUserService {
    User ValidAndSave(User user);
    List<User> getAllUsers();
    User getUserById(Integer id);
    User updateUser(Integer id, UserUpdate user);

    void changePassword(Integer userId, String actual, String nueva);
    void changePasswordByAdmin(Integer id, String nueva);
}
