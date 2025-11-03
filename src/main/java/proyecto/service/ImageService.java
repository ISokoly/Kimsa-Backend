package proyecto.service;

import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.model.Image;
import proyecto.repo.ImageRepo;
import proyecto.service.interfaces.InterImageService;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@SuppressWarnings("unused")
public class ImageService implements InterImageService {

    private final ImageRepo repo;

    private static final String ROOT_UPLOAD_DIR = "uploads";

    @Override
    public Image validAndSave(Image image) {
        if (image == null || image.getUrl() == null || image.getUrl().isBlank() || image.getTypeImage() == null) {
            throw new IllegalArgumentException("URL y tipo son obligatorios.");
        }
        return repo.save(image);
    }

    @Override
    public List<Image> getAllImages() {
        return repo.findAll();
    }

    @Override
    public Image getImageById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Image getImageByUrl(String url) {
        return repo.findByUrl(url).orElse(null);
    }

    @Override
    public void deleteImageById(Integer id) {
        repo.deleteById(id);
    }

    private Path resolveUploadDir(String tipo, String idCategoria) {
        if ("categoria".equalsIgnoreCase(tipo)) {
            return Paths.get(ROOT_UPLOAD_DIR, "categorias");
        } else if ("producto".equalsIgnoreCase(tipo)) {
            return Paths.get(ROOT_UPLOAD_DIR, "productos", idCategoria == null ? "general" : idCategoria);
        } else {
            return Paths.get(ROOT_UPLOAD_DIR, "general");
        }
    }

    private int[] getImageSize(MultipartFile file) throws IOException {
        try (var is = file.getInputStream()) {
            var img = javax.imageio.ImageIO.read(is);
            if (img == null) throw new IOException("No se pudo leer la imagen");
            return new int[]{img.getWidth(), img.getHeight()};
        }
    }

    @Override
    public Image storeImage(MultipartFile file, String name, String tipo, String categoriaId) {
        return saveOrUpdateImage(null, file, name, tipo, categoriaId);
    }

    @Override
    public Image updateImage(Integer id, MultipartFile file, String name, String tipo, String categoriaId) {
        return saveOrUpdateImage(id, file, name, tipo, categoriaId);
    }

    private Image saveOrUpdateImage(Integer id, MultipartFile file, String name, String tipo, String categoriaId) {
        try {
            Image image = (id != null)
                    ? repo.findById(id).orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + id))
                    : new Image();

            String uuid = (id != null && image.getUuid() != null && !image.getUuid().isBlank())
                    ? image.getUuid()
                    : UUID.randomUUID().toString();

            String safeName = (name == null ? "img" : name.trim()).replaceAll("[^a-zA-Z0-9\\-]", "_");
            String fileName = safeName + "-" + uuid + ".jpg";

            Path uploadDir = resolveUploadDir(tipo, categoriaId);
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(fileName);

            if (id != null && image.getUrl() != null && !image.getUrl().isBlank()) {
                try {
                    String oldFileName = Paths.get(new URI(image.getUrl()).getPath()).getFileName().toString();
                    Path oldPath = uploadDir.resolve(oldFileName);
                    if (!oldPath.equals(filePath)) {
                        Files.deleteIfExists(oldPath);
                    }
                } catch (Exception e) {
                    System.err.println("No se pudo borrar la imagen anterior: " + e.getMessage());
                }
            }

            Thumbnails.Builder<? extends java.io.InputStream> builder = Thumbnails.of(file.getInputStream())
                    .outputFormat("jpg")
                    .outputQuality(0.9f);

            int[] size = getImageSize(file);
            if (size[0] >= size[1]) {
                int W = 1200, H = 1000;
                builder.size(W, H).crop(Positions.CENTER);
            } else {
                int W = 1000, H = 1200;
                builder.size(W, H).crop(Positions.CENTER);
            }
            builder.toFile(filePath.toFile());

            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String relativePublic = "/" + uploadDir.toString().replace(File.separatorChar, '/') + "/" + fileName;
            image.setUrl(baseUrl + relativePublic);
            image.setUuid(uuid);

            if ("categoria".equalsIgnoreCase(tipo)) {
                image.setTypeImage(Image.TypeImage.Categoria);
            } else if ("producto".equalsIgnoreCase(tipo)) {
                image.setTypeImage(Image.TypeImage.Producto);
            } else {
                image.setTypeImage(Image.TypeImage.General);
            }

            return repo.save(image);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen", e);
        }
    }
}