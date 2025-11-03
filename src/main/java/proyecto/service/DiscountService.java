package proyecto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.model.Discount;
import proyecto.repo.DiscountRepo;
import proyecto.service.interfaces.InterDiscountService;

import java.util.List;

@Service
@AllArgsConstructor
@SuppressWarnings("unused")
public class DiscountService implements InterDiscountService {

    private final DiscountRepo repo;

    @Override
    public Discount validAndSave(Discount discount) {
        if (discount == null || discount.getIdProduct() == null || discount.getPercentage() == null) {
            throw new IllegalArgumentException("Producto y porcentaje son obligatorios.");
        }
        return repo.save(discount);
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return repo.findAll();
    }

    @Override
    public Discount getDiscountById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<Discount> getDiscountsByProduct(Integer idProduct) {
        return repo.findByIdProduct(idProduct);
    }

    @Override
    public void deleteDiscount(Integer id) {
        repo.deleteById(id);
    }
}