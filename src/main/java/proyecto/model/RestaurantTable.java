package proyecto.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@jakarta.persistence.Table(name = "mesas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTable;

    private String number;
    private boolean disabled;
    private boolean active;
}