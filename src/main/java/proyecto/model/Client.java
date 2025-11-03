package proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuppressWarnings("unused")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idClient;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(unique = true, length = 8, nullable = false)
    private String dni;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private LocalDate birthdate;
}