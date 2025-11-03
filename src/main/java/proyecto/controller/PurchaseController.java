package proyecto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.purchase.PurchaseCreateDTO;
import proyecto.model.Purchase;
import proyecto.repo.PurchaseRepository;
import proyecto.service.PurchaseService;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService service;
    private final PurchaseRepository repo;

    @PostMapping
    public List<Purchase> createMany(@Valid @RequestBody PurchaseCreateDTO dto){
        return service.createMany(dto);
    }

    @GetMapping
    public Page<Purchase> list(
            @RequestParam(required = false) Integer supplierId,
            @PageableDefault(size = 20, sort = "purchaseDate", direction = Sort.Direction.DESC) Pageable pageable){
        if (supplierId != null) {
            return repo.findBySupplier_IdSupplier(supplierId, pageable);
        }
        return repo.findAll(pageable);
    }
}