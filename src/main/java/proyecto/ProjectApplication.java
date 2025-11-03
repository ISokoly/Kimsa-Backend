package proyecto;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import proyecto.service.UserService;

@SpringBootApplication
@SuppressWarnings("unused")
public class ProjectApplication {
    private final UserService userService;

    public ProjectApplication(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner init() {
        return args -> userService.encryptPasswordsExistingUsers();
    }
}