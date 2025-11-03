package proyecto.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.model.Brand;
import proyecto.repo.BrandRepo;
import proyecto.repo.CategoryRepo;
import proyecto.service.interfaces.InterBrandService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@SuppressWarnings("unused")
public class BrandService implements InterBrandService {
    @Autowired
    public final BrandRepo repo;
    @Autowired
    public final CategoryRepo categoryRepo;

    @Override
    public Brand ValidAndSave(Brand brand) {
        if (brand == null || brand.getName() == null || brand.getName().isBlank()) {
            throw new IllegalArgumentException("Nombre es obligatorio.");
        }
        if (brand.getCategory() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        boolean exists = categoryRepo.existsById(brand.getCategory());
        if (!exists) {
            throw new IllegalArgumentException("No se encontró la categoría con id " + brand.getCategory());
        }
        return repo.save(brand);
    }

    @Override
    public List<Brand> getAllBrands() {
        return repo.findAll();
    }

    @Override
    public Brand getBrandById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void deleteBrand(Integer id) {
        Optional<Brand> existing = repo.findById(id);
        if (existing.isPresent()) {
            repo.deleteById(Math.toIntExact(id));
        } else {
            throw new IllegalArgumentException("La marca con id " + id + " no existe.");
        }
    }
}