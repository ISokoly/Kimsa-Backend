// src/main/java/proyecto/config/WebConfig.java
package proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer webConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry r) {
                r.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                String abs = Paths.get("uploads").toFile().getAbsolutePath();
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + abs + "/");
            }
        };
    }
}