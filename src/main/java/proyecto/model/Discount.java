package proyecto.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuppressWarnings("unused")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDiscount;

    private Integer idProduct;
    @Column(nullable = false)
    private Integer percentage;
    private boolean disabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayWeek typeDay = DayWeek.General;

    public enum DayWeek {
        Lunes,
        Martes,
        Miercoles,
        Jueves,
        Viernes,
        Sabado,
        Domingo,
        General;

        @JsonCreator
        public static DayWeek fromString(String value) {
            for (DayWeek d : DayWeek.values()) {
                if (d.name().equalsIgnoreCase(value)) {
                    return d;
                }
            }
            throw new IllegalArgumentException("Valor inválido para DayWeek: " + value);
        }
    }
}