package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.model.Image;
import proyecto.service.interfaces.InterImageService;

import java.util.List;

@SuppressWarnings("unused")
@RequestMapping("/api/images")
@RestController
@RequiredArgsConstructor
public class ImageController {
    public final InterImageService service;

    @PostMapping
    public ResponseEntity<Image> addImage(@RequestBody Image image) {
        Image savedImage = service.validAndSave(image);
        return ResponseEntity.ok(savedImage);
    }

    @GetMapping
    public List<Image> getAllImages() {
        return service.getAllImages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Integer id) {
        Image image = service.getImageById(id);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(image);
    }

    @GetMapping("/url/{url}")
    public ResponseEntity<Image> getImageByUrl(@RequestParam String url) {
        Image image = service.getImageByUrl(url);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(image);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable Integer id) {
        service.deleteImageById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<Image> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nombre") String nombre,
            @RequestParam("tipo") String tipo,
            @RequestParam("categoria") String categoriaId) {
        return ResponseEntity.ok(service.storeImage(file, nombre, tipo, categoriaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImage(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("nombre") String nombre,
            @RequestParam("tipo") String tipo,
            @RequestParam("categoria") String categoriaId) {
        return ResponseEntity.ok(service.updateImage(id, file, nombre, tipo, categoriaId));
    }
}