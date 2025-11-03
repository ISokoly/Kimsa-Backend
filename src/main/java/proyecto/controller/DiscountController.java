package proyecto.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.model.Discount;
import proyecto.service.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@AllArgsConstructor
@SuppressWarnings("unused")
public class DiscountController {

    private final DiscountService service;

    @GetMapping
    public List<Discount> getAll() {
        return service.getAllDiscounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Discount> getById(@PathVariable Integer id) {
        Discount discount = service.getDiscountById(id);
        return discount != null ? ResponseEntity.ok(discount) : ResponseEntity.notFound().build();
    }

    @GetMapping("/product/{idProduct}")
    public List<Discount> getByProduct(@PathVariable Integer idProduct) {
        return service.getDiscountsByProduct(idProduct);
    }

    @PostMapping
    public Discount create(@RequestBody Discount discount) {
        return service.validAndSave(discount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discount> update(@PathVariable Integer id, @RequestBody Discount discount) {
        Discount existing = service.getDiscountById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        existing.setDisabled(discount.isDisabled());
        existing.setPercentage(discount.getPercentage());
        existing.setTypeDay(discount.getTypeDay());
        discount.setIdDiscount(id);
        return ResponseEntity.ok(service.validAndSave(discount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
}