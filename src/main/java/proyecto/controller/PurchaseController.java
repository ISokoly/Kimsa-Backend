package proyecto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import proyecto.dto.purchase.PurchaseCreateDTO;
import proyecto.dto.purchase.PurchaseUpdateDTO;
import proyecto.model.Purchase;
import proyecto.repo.PurchaseRepository;
import proyecto.service.PurchaseService;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService service;
    private final PurchaseRepository repo;

    @PostMapping
    public Purchase create(@Valid @RequestBody PurchaseCreateDTO dto) {
        return service.create(dto);
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

    @GetMapping("/{id}")
    public Purchase getById(@PathVariable Integer id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada")
                );
    }

    @PutMapping("/{id}")
    public Purchase updateOne(
            @PathVariable Integer id,
            @Valid @RequestBody PurchaseUpdateDTO dto
    ) {
        try {
            return service.update(id, dto);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }
}
