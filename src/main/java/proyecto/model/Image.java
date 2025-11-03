package proyecto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idImage;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeImage typeImage = TypeImage.General;

    @Column(length = 36, unique = true, nullable = false)
    private String uuid = UUID.randomUUID().toString();

    public enum TypeImage {
        Categoria,
        Producto,
        General
    }
}
