package proyecto.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import proyecto.model.User;
import proyecto.model.Client;
import proyecto.repo.UserRepo;
import proyecto.repo.ClientRepo;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepo userRepo;
    private final ClientRepo clientRepo;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepo.count() == 0) {
            User admin = new User();
            admin.setName("Administrador");
            admin.setLastName("Principal");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setDni("00000000");
            admin.setNumberPhone("000000000");
            admin.setDirection("Dirección por defecto");
            admin.setRol(User.Rol.Administrator);
            admin.setAdministratorPermissions(true);
            admin.setDisabled(false);
            userRepo.save(admin);

            User employee = new User();
            employee.setName("Empleado");
            employee.setLastName("Default");
            employee.setUsername("employee");
            employee.setPassword(passwordEncoder.encode("employee123"));
            employee.setDni("00000001");
            employee.setNumberPhone("000000000");
            employee.setDirection("Dirección por defecto");
            employee.setRol(User.Rol.Employee);
            employee.setAdministratorPermissions(false);
            employee.setDisabled(false);
            userRepo.save(employee);
        }

        if (clientRepo.count() == 0) {
            Client generico = new Client();
            generico.setName("Generico");
            generico.setDni("00000002");
            generico.setBirthdate(LocalDate.of(2000, 1, 1));
            clientRepo.save(generico);

        }
    }
}