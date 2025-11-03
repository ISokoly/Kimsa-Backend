package proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(unique = true, length = 8, nullable = false)
    private String dni;

    @Column(length = 9, nullable = false)
    private String numberPhone;

    @Column(nullable = false)
    private String direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean disabled = false;

    @Column(nullable = false)
    private boolean administratorPermissions = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public enum Rol {
        Administrator,
        Employee
    }
}