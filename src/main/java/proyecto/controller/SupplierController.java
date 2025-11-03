package proyecto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import proyecto.model.Supplier;
import proyecto.repo.SupplierRepository;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierRepository repo;

    @GetMapping public List<Supplier> list(){ return repo.findAll(); }

    @PostMapping public Supplier create(@Valid @RequestBody Supplier s){ return repo.save(s); }

    @PutMapping("/{id}")
    public Supplier update(@PathVariable Integer id, @Valid @RequestBody Supplier s){
        Supplier db = repo.findById(id).orElseThrow();
        db.setName(s.getName());
        db.setPhone(s.getPhone());
        db.setAddress(s.getAddress());
        db.setEmail(s.getEmail());
        db.setActive(s.isActive());
        return repo.save(db);
    }

    @PatchMapping("/{id}/toggle")
    public void toggle(@PathVariable Integer id){
        Supplier db = repo.findById(id).orElseThrow();
        db.setActive(!db.isActive());
        repo.save(db);
    }
}
