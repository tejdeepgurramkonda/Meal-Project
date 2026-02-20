package com.example.backend.service;

import com.example.backend.dto.MealDTO;
import com.example.backend.dto.MealResponse;
import com.example.backend.entity.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealImportService {

    private final MealRepository mealRepository;
    private final CategoryRepository categoryRepository;
    private final AreaRepository areaRepository;
    private final IngredientRepository ingredientRepository;
    private final MealIngredientRepository mealIngredientRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_BASE_URL = "https://www.themealdb.com/api/json/v1/1/search.php?f=";

    @Transactional
    public void importAllMeals() {
        log.info("Starting meal import from TheMealDB...");
        int totalMeals = 0;

        // Loop through a-z
        for (char letter = 'a'; letter <= 'z'; letter++) {
            try {
                log.info("Fetching meals starting with '{}'", letter);
                String url = API_BASE_URL + letter;
                MealResponse response = restTemplate.getForObject(url, MealResponse.class);

                if (response != null && response.getMeals() != null) {
                    for (MealDTO mealDTO : response.getMeals()) {
                        try {
                            importMeal(mealDTO);
                            totalMeals++;
                        } catch (Exception e) {
                            log.error("Error importing meal: {}", mealDTO.getStrMeal(), e);
                        }
                    }
                }
                
                // Small delay to avoid overwhelming the API
                Thread.sleep(200);
            } catch (Exception e) {
                log.error("Error fetching meals for letter '{}'", letter, e);
            }
        }

        log.info("Meal import completed! Total meals imported: {}", totalMeals);
    }

    @Transactional
    public void importMeal(MealDTO mealDTO) {
        Integer externalId = Integer.parseInt(mealDTO.getIdMeal());

        // Check if meal already exists
        if (mealRepository.findByExternalId(externalId).isPresent()) {
            log.debug("Meal with externalId {} already exists, skipping", externalId);
            return;
        }

        // Save or get Category
        Category category = getOrCreateCategory(mealDTO.getStrCategory());

        // Save or get Area
        Area area = getOrCreateArea(mealDTO.getStrArea());

        // Create and save Meal
        Meal meal = new Meal();
        meal.setExternalId(externalId);
        meal.setName(mealDTO.getStrMeal());
        meal.setCategory(category);
        meal.setArea(area);
        meal.setInstructions(mealDTO.getStrInstructions());
        meal.setThumbnailUrl(mealDTO.getStrMealThumb());
        meal.setYoutubeUrl(mealDTO.getStrYoutube());
        meal.setTags(mealDTO.getStrTags());
        meal.setIsExternal(true);
        meal.setCreatedBy(null); // External meals have no user

        meal = mealRepository.save(meal);
        log.info("Saved meal: {}", meal.getName());

        // Process ingredients (1-20)
        saveIngredients(meal, mealDTO);
    }

    private void saveIngredients(Meal meal, MealDTO mealDTO) {
        String[] ingredients = {
            mealDTO.getStrIngredient1(), mealDTO.getStrIngredient2(), mealDTO.getStrIngredient3(),
            mealDTO.getStrIngredient4(), mealDTO.getStrIngredient5(), mealDTO.getStrIngredient6(),
            mealDTO.getStrIngredient7(), mealDTO.getStrIngredient8(), mealDTO.getStrIngredient9(),
            mealDTO.getStrIngredient10(), mealDTO.getStrIngredient11(), mealDTO.getStrIngredient12(),
            mealDTO.getStrIngredient13(), mealDTO.getStrIngredient14(), mealDTO.getStrIngredient15(),
            mealDTO.getStrIngredient16(), mealDTO.getStrIngredient17(), mealDTO.getStrIngredient18(),
            mealDTO.getStrIngredient19(), mealDTO.getStrIngredient20()
        };

        String[] measures = {
            mealDTO.getStrMeasure1(), mealDTO.getStrMeasure2(), mealDTO.getStrMeasure3(),
            mealDTO.getStrMeasure4(), mealDTO.getStrMeasure5(), mealDTO.getStrMeasure6(),
            mealDTO.getStrMeasure7(), mealDTO.getStrMeasure8(), mealDTO.getStrMeasure9(),
            mealDTO.getStrMeasure10(), mealDTO.getStrMeasure11(), mealDTO.getStrMeasure12(),
            mealDTO.getStrMeasure13(), mealDTO.getStrMeasure14(), mealDTO.getStrMeasure15(),
            mealDTO.getStrMeasure16(), mealDTO.getStrMeasure17(), mealDTO.getStrMeasure18(),
            mealDTO.getStrMeasure19(), mealDTO.getStrMeasure20()
        };

        for (int i = 0; i < ingredients.length; i++) {
            String ingredientName = ingredients[i];
            String measure = measures[i];

            // Skip if ingredient is empty or null
            if (ingredientName == null || ingredientName.trim().isEmpty() || 
                ingredientName.trim().equalsIgnoreCase("null")) {
                continue;
            }

            ingredientName = ingredientName.trim();
            measure = (measure == null || measure.trim().isEmpty()) ? "To taste" : measure.trim();

            // Get or create ingredient
            Ingredient ingredient = getOrCreateIngredient(ingredientName);

            // Create MealIngredient relationship
            MealIngredient mealIngredient = new MealIngredient();
            mealIngredient.setMeal(meal);
            mealIngredient.setIngredient(ingredient);
            mealIngredient.setMeasure(measure);

            mealIngredientRepository.save(mealIngredient);
        }
    }

    private Category getOrCreateCategory(String categoryName) {
        final String finalCategoryName = (categoryName == null || categoryName.trim().isEmpty()) 
            ? "Uncategorized" 
            : categoryName;

        return categoryRepository.findByName(finalCategoryName)
            .orElseGet(() -> {
                Category category = new Category();
                category.setName(finalCategoryName);
                category.setExternalId(null);
                return categoryRepository.save(category);
            });
    }

    private Area getOrCreateArea(String areaName) {
        final String finalAreaName = (areaName == null || areaName.trim().isEmpty()) 
            ? "Unknown" 
            : areaName;

        return areaRepository.findByName(finalAreaName)
            .orElseGet(() -> {
                Area area = new Area();
                area.setName(finalAreaName);
                area.setExternalId(null);
                return areaRepository.save(area);
            });
    }

    private Ingredient getOrCreateIngredient(String ingredientName) {
        return ingredientRepository.findByName(ingredientName)
            .orElseGet(() -> {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(ingredientName);
                ingredient.setExternalId(null);
                return ingredientRepository.save(ingredient);
            });
    }
}
