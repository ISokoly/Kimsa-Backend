package proyecto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import proyecto.dto.recipe.RecipeSummaryDTO;
import proyecto.dto.recipe.RecipeUpsertDTO;
import proyecto.model.Recipe;
import proyecto.model.RecipeDetail;
import proyecto.service.RecipeService;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService service;

    @PostMapping
    public Recipe create(@Valid @RequestBody RecipeUpsertDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public Recipe update(@PathVariable Integer id, @Valid @RequestBody RecipeUpsertDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping
    public List<RecipeSummaryDTO> list() {
        return service.list();
    }

    @GetMapping("/{id}/details")
    public List<RecipeDetail> details(@PathVariable Integer id) {
        return service.details(id);
    }

    @GetMapping("/product/{idProduct}")
    public RecipeSummaryDTO findByProduct(@PathVariable Integer idProduct) {
        return service.findByProduct(idProduct);
    }

    @GetMapping("/product/{idProduct}/details")
    public List<RecipeDetail> detailsByProduct(@PathVariable Integer idProduct) {
        return service.detailsByProduct(idProduct);
    }
}
