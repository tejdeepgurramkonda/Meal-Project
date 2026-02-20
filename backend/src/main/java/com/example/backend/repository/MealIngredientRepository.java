package com.example.backend.repository;

import com.example.backend.entity.MealIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealIngredientRepository extends JpaRepository<MealIngredient, Long> {
    List<MealIngredient> findByMealId(Long mealId);
    List<MealIngredient> findByIngredientId(Long ingredientId);
    void deleteByMealId(Long mealId);
}
