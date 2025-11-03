// src/main/java/proyecto/web/UploadsController.java
package proyecto.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.*;

@RestController
@RequestMapping("/uploads")
public class UploadsController {

    private static final Path ROOT = Paths.get("uploads");
    private static final String PLACEHOLDER_CLASSPATH = "image/placeholder.png";

    private static Resource resolvePlaceholder() {
        Resource r = new ClassPathResource(PLACEHOLDER_CLASSPATH);
        if (r.exists() && r.isReadable()) return r;
        return new ByteArrayResource(TINY_PNG_BYTES) {
            @Override public String getFilename() { return "placeholder.png"; }
        };
    }

    @GetMapping("/**")
    public ResponseEntity<Resource> get(HttpServletRequest request) {
        try {
            String uri = request.getRequestURI();
            String subpath = uri.replaceFirst("^/uploads/?", "");
            Path path = ROOT.resolve(subpath).normalize();

            if (!path.startsWith(ROOT)) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resolvePlaceholder());
            }

            Resource res = new UrlResource(path.toUri());
            if (res.exists() && res.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(guessContentType(path))
                        .cacheControl(CacheControl.maxAge(java.time.Duration.ofHours(1)).cachePublic())
                        .body(res);
            }
        } catch (MalformedURLException ignored) {}

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noCache())
                .body(resolvePlaceholder());
    }

    private static MediaType guessContentType(Path p) {
        String fn = p.getFileName().toString().toLowerCase();
        if (fn.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (fn.endsWith(".gif")) return MediaType.IMAGE_GIF;
        return MediaType.IMAGE_JPEG;
    }

    private static final byte[] TINY_PNG_BYTES = new byte[] {
            (byte)0x89,0x50,0x4E,0x47,0x0D,0x0A,0x1A,0x0A,0x00,0x00,0x00,0x0D,0x49,0x48,0x44,0x52,
            0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x01,0x08,0x06,0x00,0x00,0x00,0x1F,0x15, (byte) 0xC4, (byte) 0x89,
            0x00,0x00,0x00,0x0A,0x49,0x44,0x41,0x54,0x78, (byte) 0x9C,0x63,0x60,0x00,0x00,0x00,0x02,0x00,
            0x01, (byte) 0xE5,0x27, (byte) 0xD4, (byte) 0xA0,0x00,0x00,0x00,0x00,0x49,0x45,0x4E,0x44, (byte) 0xAE,0x42,0x60, (byte) 0x82
    };
}
