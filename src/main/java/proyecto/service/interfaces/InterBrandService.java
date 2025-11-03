package proyecto.service.interfaces;

import proyecto.model.Brand;

import java.nio.channels.FileChannel;
import java.util.List;

public interface InterBrandService {
    Brand ValidAndSave(Brand brand);
    List<Brand> getAllBrands();
    Brand getBrandById(Integer id);
    void deleteBrand(Integer id);
}
