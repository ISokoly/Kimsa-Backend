package proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "Suppliers")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSupplier;

    @Column(nullable = false, length = 150)
    @NotBlank
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "text")
    private String address;

    @Column(length = 150)
    @Email
    private String email;

    @Column(nullable = false)
    private boolean active = true;
}
