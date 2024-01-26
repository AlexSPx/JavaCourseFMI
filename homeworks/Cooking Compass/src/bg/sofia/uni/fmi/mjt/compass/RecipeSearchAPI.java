package bg.sofia.uni.fmi.mjt.compass;

import bg.sofia.uni.fmi.mjt.compass.exceptions.MissingParametersException;
import bg.sofia.uni.fmi.mjt.compass.exceptions.RequestFailedException;
import bg.sofia.uni.fmi.mjt.compass.health.HealthLabel;
import bg.sofia.uni.fmi.mjt.compass.meal.MealType;

import java.util.List;

/**
 * An interface for searching recipes.
 */
public interface RecipeSearchAPI {
    /**
     * Adds a keyword to the recipe search.
     *
     * @param keyword the keyword to be added
     * @return the updated instance of RecipeSearchAPI
     * @throws IllegalArgumentException if the keyword is null or empty
     */
    RecipeSearchAPI addKeyword(String keyword);

    /**
     * Adds a meal type to the recipe search.
     *
     * @param type the meal type to be added
     * @return the updated instance of RecipeSearchAPI
     * @throws IllegalArgumentException if the meal type is null
     */
    RecipeSearchAPI addMealType(MealType type);

    /**
     * Adds a health label to the recipe search.
     *
     * @param label the health label to be added
     * @return the updated instance of RecipeSearchAPI
     * @throws IllegalArgumentException if the health label is null
     */
    RecipeSearchAPI addHealthLabel(HealthLabel label);

    /**
     * Queries the API for recipes based on the provided parameters.
     *
     * @return a list of Recipe objects matching the search criteria
     * @throws RequestFailedException if the API request fails
     * @throws MissingParametersException if no parameters are provided for the search
     */
    List<Recipe> query() throws RequestFailedException, MissingParametersException;
}
