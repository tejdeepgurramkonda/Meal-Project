package com.example.backend;

import com.example.backend.repository.MealRepository;
import com.example.backend.service.MealImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Slf4j
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	CommandLineRunner run(MealImportService mealImportService, MealRepository mealRepository) {
		return args -> {
			// Check if meals already exist in database
			long mealCount = mealRepository.count();
			if (mealCount > 0) {
				log.info("Database already contains {} meals. Skipping import.", mealCount);
				return;
			}
			
			// Import meals from TheMealDB (runs only once)
			log.info("Database is empty. Starting meal import from TheMealDB...");
			mealImportService.importAllMeals();
		};
	}

}
