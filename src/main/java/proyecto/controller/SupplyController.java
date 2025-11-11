package proyecto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.model.Category;
import proyecto.model.Supply;
import proyecto.repo.SupplyRepository;

import java.util.List;

@RestController
@RequestMapping("/api/supplies")
@RequiredArgsConstructor
public class SupplyController {
    private final SupplyRepository repo;

    @GetMapping
    public List<Supply> list() {
        return repo.findAll();
    }

    @PostMapping
    public Supply create(@Valid @RequestBody Supply s) {
        return repo.save(s);
    }

    @PutMapping("/{id}")
    public Supply update(@PathVariable Integer id, @Valid @RequestBody Supply s) {
        Supply db = repo.findById(id).orElseThrow();
        db.setName(s.getName());
        db.setUnitPrice(s.getUnitPrice());
        db.setUnit(s.getUnit());
        db.setActive(s.isActive());
        return repo.save(db);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supply> getSupplyById(@PathVariable Integer id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
