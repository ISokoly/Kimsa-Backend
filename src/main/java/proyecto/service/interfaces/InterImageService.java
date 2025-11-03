package proyecto.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import proyecto.model.Image;

import java.io.IOException;
import java.util.List;

public interface InterImageService {
    Image validAndSave(Image image);
    List<Image> getAllImages();
    Image getImageById(Integer id);
    Image getImageByUrl(String url);
    void deleteImageById(Integer id);

    Image storeImage(MultipartFile file, String nombre, String tipo, String categoriaId);
    Image updateImage(Integer id, MultipartFile file, String nombre, String tipo, String categoriaId);
}