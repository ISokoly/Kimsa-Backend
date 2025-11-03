package proyecto.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyecto.model.RestaurantTable;
import proyecto.repo.TableRepo;
import proyecto.service.interfaces.InterCategoryService;
import proyecto.service.interfaces.InterTableService;

import java.util.List;
import java.util.Optional;

@Service
public class TableService implements InterTableService {

    private final TableRepo repo;

    public TableService(TableRepo mesaRepository) {
        this.repo = mesaRepository;
    }

    public List<RestaurantTable> getTables() {
        List<RestaurantTable> mesas = repo.findAll();

        return mesas.stream()
                .sorted((a, b) -> {
                    if (a.getNumber().equalsIgnoreCase("delivery")) return 1;
                    if (b.getNumber().equalsIgnoreCase("delivery")) return -1;
                    try {
                        int numA = Integer.parseInt(a.getNumber().replaceAll("[^0-9]", ""));
                        int numB = Integer.parseInt(b.getNumber().replaceAll("[^0-9]", ""));
                        return Integer.compare(numA, numB);
                    } catch (NumberFormatException e) {
                        return a.getNumber().compareToIgnoreCase(b.getNumber());
                    }
                })
                .toList();
    }

    public Optional<RestaurantTable> getTableById(Long id) {
        return repo.findById(Math.toIntExact(id));
    }

    public List<RestaurantTable> getActiveTables() {
        return repo.findByDisabledFalse();
    }

    @Transactional
    public void createMultiples(int cantidad) {
        repo.deleteAll();
        for (int i = 1; i <= cantidad; i++) {
            repo.save(new RestaurantTable(null, String.valueOf(i), false, false));
        }
        repo.save(new RestaurantTable(null, "delivery", false, false));
    }

    @Transactional
    public void updateQuantity(int cantidad) {
        List<RestaurantTable> actuales = repo.findAll();
        long actualesCount = actuales.stream().filter(m -> !m.getNumber().equals("delivery")).count();

        if (cantidad <= 0) {
            repo.deleteAll();
            repo.save(new RestaurantTable(null, "delivery", false, false));
            return;
        }

        if (actualesCount > cantidad) {
            actuales.stream()
                    .filter(m -> !m.getNumber().equals("delivery"))
                    .sorted((a, b) -> Long.compare(b.getIdTable(), a.getIdTable()))
                    .limit(actualesCount - cantidad)
                    .forEach(m -> repo.deleteById(Math.toIntExact(m.getIdTable())));
        } else if (actualesCount < cantidad) {
            for (int i = (int) actualesCount + 1; i <= cantidad; i++) {
                repo.save(new RestaurantTable(null, String.valueOf(i), false, false));
            }
        }
    }

    @Transactional
    public void updateState(List<RestaurantTable> cambios) {
        for (RestaurantTable cambio : cambios) {
            repo.findById(Math.toIntExact(cambio.getIdTable())).ifPresent(mesa -> {
                mesa.setDisabled(cambio.isDisabled());
                repo.save(mesa);
            });
        }
    }

    @Transactional
    public RestaurantTable updateMesa(Long id, RestaurantTable data) {
        return repo.findById(Math.toIntExact(id)).map(mesa -> {
            mesa.setDisabled(data.isDisabled());
            mesa.setActive(data.isActive());
            return repo.save(mesa);
        }).orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
    }
}
