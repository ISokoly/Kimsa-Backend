package proyecto.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.dto.recipe.RecipeSummaryDTO;
import proyecto.dto.recipe.RecipeUpsertDTO;
import proyecto.model.*;
import proyecto.repo.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepo;
    private final RecipeDetailRepository detailRepo;
    private final SupplyRepository supplyRepo;
    private final ProductRepo productRepo;

    @Transactional
    public Recipe create(RecipeUpsertDTO dto) {
        Product product = productRepo.findById(dto.idProduct())
                .orElseThrow(() -> new IllegalArgumentException("Product no encontrado"));

        Recipe recipe = new Recipe();
        recipe.setProduct(product);
        Recipe saved = recipeRepo.save(recipe);

        // Agrupar items por idSupply para evitar duplicados
        Map<Integer, BigDecimal> grouped = dto.items().stream()
                .collect(Collectors.toMap(
                        RecipeUpsertDTO.Line::idSupply,
                        RecipeUpsertDTO.Line::gramsQuantity,
                        BigDecimal::add
                ));

        for (Map.Entry<Integer, BigDecimal> e : grouped.entrySet()) {
            Integer idSupply = e.getKey();
            BigDecimal grams = e.getValue();

            Supply supply = supplyRepo.findById(idSupply)
                    .orElseThrow(() -> new IllegalArgumentException("Supply no encontrado: " + idSupply));

            RecipeDetail det = new RecipeDetail();
            det.setRecipe(saved);
            det.setSupply(supply);
            det.setGramsQuantity(grams);
            detailRepo.save(det);
        }

        return saved;
    }

    @Transactional
    public Recipe update(Integer idRecipe, RecipeUpsertDTO dto) {
        Recipe recipe = recipeRepo.findById(idRecipe)
                .orElseThrow(() -> new IllegalArgumentException("Recipe no encontrada"));

        // Si permites cambiar el product, actualízalo; si no, valida:
        if (!recipe.getProduct().getIdProduct().equals(dto.idProduct())) {
            Product product = productRepo.findById(dto.idProduct())
                    .orElseThrow(() -> new IllegalArgumentException("Product no encontrado"));
            recipe.setProduct(product);
            recipeRepo.save(recipe);
        }

        // Agrupar entrantes para evitar duplicados (y 23505)
        Map<Integer, BigDecimal> incoming = dto.items().stream()
                .collect(Collectors.toMap(
                        RecipeUpsertDTO.Line::idSupply,
                        RecipeUpsertDTO.Line::gramsQuantity,
                        BigDecimal::add
                ));

        // Mapa de existentes por idSupply
        List<RecipeDetail> existing = detailRepo.findByRecipe_IdRecipe(idRecipe);
        Map<Integer, RecipeDetail> bySupply = new HashMap<>();
        for (RecipeDetail d : existing) {
            bySupply.put(d.getSupply().getIdSupply(), d);
        }

        // Upsert
        Set<Integer> seen = new HashSet<>();
        for (Map.Entry<Integer, BigDecimal> e : incoming.entrySet()) {
            Integer idSupply = e.getKey();
            BigDecimal grams = e.getValue();
            seen.add(idSupply);

            RecipeDetail current = bySupply.get(idSupply);
            if (current != null) {
                // update
                current.setGramsQuantity(grams);
                detailRepo.save(current);
            } else {
                // insert
                Supply supply = supplyRepo.findById(idSupply)
                        .orElseThrow(() -> new IllegalArgumentException("Supply no encontrado: " + idSupply));
                RecipeDetail det = new RecipeDetail();
                det.setRecipe(recipe);
                det.setSupply(supply);
                det.setGramsQuantity(grams);
                detailRepo.save(det);
            }
        }

        // Delete los que ya no están
        for (RecipeDetail prev : existing) {
            int sid = prev.getSupply().getIdSupply();
            if (!seen.contains(sid)) {
                detailRepo.delete(prev);
            }
        }

        return recipe;
    }

    public List<RecipeSummaryDTO> list() {
        List<Recipe> recs = recipeRepo.findAllWithProduct(); // con product
        List<RecipeSummaryDTO> out = new ArrayList<>(recs.size());

        for (Recipe r : recs) {
            long count = detailRepo.countByRecipe_IdRecipe(r.getIdRecipe());
            out.add(new RecipeSummaryDTO(
                    r.getIdRecipe(),
                    r.getProduct() != null ? r.getProduct().getIdProduct() : null,
                    r.getProduct() != null ? r.getProduct().getName() : null,
                    count
            ));
        }
        return out;
    }

    public List<RecipeDetail> details(Integer idRecipe) {
        return detailRepo.findByRecipe_IdRecipe(idRecipe);
    }

    public RecipeSummaryDTO findByProduct(Integer idProduct) {
        Recipe r = recipeRepo.findByProduct_IdProduct(idProduct)
                .orElseThrow(() -> new IllegalArgumentException("Recipe no encontrada para product " + idProduct));

        long count = detailRepo.countByRecipe_IdRecipe(r.getIdRecipe());
        return new RecipeSummaryDTO(
                r.getIdRecipe(),
                r.getProduct() != null ? r.getProduct().getIdProduct() : null,
                r.getProduct() != null ? r.getProduct().getName() : null,
                count
        );
    }

    public List<RecipeDetail> detailsByProduct(Integer idProduct) {
        Recipe r = recipeRepo.findByProduct_IdProduct(idProduct)
                .orElseThrow(() -> new IllegalArgumentException("Recipe no encontrada para product " + idProduct));
        return detailRepo.findByRecipe_IdRecipe(r.getIdRecipe());
    }
}
