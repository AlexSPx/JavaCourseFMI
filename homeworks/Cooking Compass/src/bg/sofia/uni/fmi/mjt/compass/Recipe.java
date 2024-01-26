package bg.sofia.uni.fmi.mjt.compass;

import bg.sofia.uni.fmi.mjt.compass.cuisine.CuisineType;
import bg.sofia.uni.fmi.mjt.compass.dish.DishType;
import bg.sofia.uni.fmi.mjt.compass.health.HealthLabel;
import bg.sofia.uni.fmi.mjt.compass.meal.MealType;

import java.math.BigDecimal;
import java.util.List;

public record Recipe(String label, List<HealthLabel> healthLabels, BigDecimal totalWeight,
                     List<CuisineType> cuisineType, List<MealType> mealType, List<DishType> dishType,
                     List<String> ingredientLines) {
}
