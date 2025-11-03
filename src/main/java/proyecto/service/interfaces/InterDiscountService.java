package proyecto.service.interfaces;

import proyecto.model.Discount;

import java.util.List;

public interface InterDiscountService {
    Discount validAndSave(Discount discount);
    List<Discount> getAllDiscounts();
    Discount getDiscountById(Integer id);
    List<Discount> getDiscountsByProduct(Integer idProduct);
    void deleteDiscount(Integer id);
}