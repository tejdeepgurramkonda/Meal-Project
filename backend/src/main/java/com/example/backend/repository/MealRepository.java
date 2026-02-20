package com.example.backend.repository;

import com.example.backend.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    Optional<Meal> findByExternalId(Integer externalId);
    List<Meal> findByIsExternal(Boolean isExternal);
    List<Meal> findByCategoryId(Long categoryId);
    List<Meal> findByAreaId(Long areaId);
    List<Meal> findByCreatedById(Long userId);
    List<Meal> findByNameContainingIgnoreCase(String name);
}
