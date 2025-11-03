package proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.MesaUpdateDTO;
import proyecto.model.RestaurantTable;
import proyecto.repo.TableRepo;
import proyecto.service.TableService;

import java.util.*;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TableController {

    @Autowired
    public final TableRepo repo;

    @Autowired
    public final TableService service;

    @GetMapping
    public List<RestaurantTable> getMesas() {
        return repo.findAll();
    }

    @GetMapping("/disables")
    public List<RestaurantTable> getDisableTables() {
        return repo.findByDisabledFalse();
    }

    @GetMapping("/actives")
    public List<RestaurantTable> getActiveTables() {
        return service.getActiveTables();
    }

    @PostMapping("/add-multiples")
    public List<RestaurantTable> createTables(@RequestBody Map<String, Integer> body) {
        int cantidad = body.get("cantidad");
        List<RestaurantTable> mesas = new ArrayList<>();

        for (int i = 1; i <= cantidad; i++) {
            RestaurantTable mesa = new RestaurantTable(null, String.valueOf(i), false, false);
            mesas.add(repo.save(mesa));
        }
        return mesas;
    }

    @PostMapping("/update-quantity")
    public List<RestaurantTable> updateQuantity(@RequestBody Map<String, Integer> body) {
        int cantidad = body.get("cantidad");
        List<RestaurantTable> actuales = repo.findAll();

        long activas = actuales.stream().filter(m -> !m.isDisabled()).count();
        if (cantidad < activas) {
            throw new RuntimeException("❌ No puedes reducir la cantidad por debajo de las mesas activas (" + activas + ")");
        }

        if (cantidad > actuales.size()) {
            for (int i = actuales.size() + 1; i <= cantidad; i++) {
                repo.save(new RestaurantTable(null, String.valueOf(i), false, false));
            }
        } else if (cantidad < actuales.size()) {
            for (int i = actuales.size(); i > cantidad; i--) {
                repo.deleteById(i);
            }
        }
        return repo.findAll();
    }


    @PutMapping("/update-state")
    public List<RestaurantTable> updateState(@RequestBody List<MesaUpdateDTO> cambios) {
        for (MesaUpdateDTO cambio : cambios) {
            if (cambio.getIdTable() == null) continue;
            repo.findById(cambio.getIdTable()).ifPresent(mesa -> {
                mesa.setDisabled(cambio.isDisabled());
                repo.save(mesa);
            });
        }
        return repo.findAll();
    }

    @PutMapping("/{id}")
    public RestaurantTable updateTable(@PathVariable Integer id, @RequestBody RestaurantTable datos) {
        return repo.findById(id).map(mesa -> {
            mesa.setDisabled(datos.isDisabled());
            mesa.setActive(datos.isActive());
            return repo.save(mesa);
        }).orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
    }
}
