// src/main/java/proyecto/dto/ClientEnsureDTO.java
package proyecto.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClientEnsureDTO {
    private String name;
    private String dni;
    private LocalDate birthdate;
}
