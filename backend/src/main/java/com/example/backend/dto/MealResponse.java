package com.example.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class MealResponse {
    private List<MealDTO> meals;
}
