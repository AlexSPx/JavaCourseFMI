package bg.sofia.uni.fmi.mjt.compass;

import bg.sofia.uni.fmi.mjt.compass.cuisine.CuisineType;
import bg.sofia.uni.fmi.mjt.compass.dish.DishType;
import bg.sofia.uni.fmi.mjt.compass.exceptions.MissingParametersException;
import bg.sofia.uni.fmi.mjt.compass.exceptions.RequestFailedException;
import bg.sofia.uni.fmi.mjt.compass.health.HealthLabel;
import bg.sofia.uni.fmi.mjt.compass.meal.MealType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeSearchTest {
    @Mock
    private HttpClient clientMock;

    @InjectMocks
    private RecipeSearch recipeSearch;

    @Test
    void noParametersSearchTest() {
        assertThrows(MissingParametersException.class,
                recipeSearch::query);
    }

    @Test
    void nullMealTypeTest() {
        assertThrows(IllegalArgumentException.class,
                () -> recipeSearch.addMealType(null),
                "MealType must not be null");
    }

    @Test
    void nullHealthLabelTest() {
        assertThrows(IllegalArgumentException.class,
                () -> recipeSearch.addHealthLabel(null),
                "HealthLabel must not be null");
    }

    @Test
    void nullKeywordTest() {
        assertThrows(IllegalArgumentException.class,
                () -> recipeSearch.addKeyword(null),
                "Keyword must not be null");
    }

    @Test
    void emptyKeywordTest() {
        assertThrows(IllegalArgumentException.class,
                () -> recipeSearch.addKeyword(""),
                "Keyword must not be empty");
    }

    @Test
    void queryNextTest() throws IOException, InterruptedException, RequestFailedException, MissingParametersException {
        List<HealthLabel> healthLabels = List.of(HealthLabel.KIDNEY_FRIENDLY, HealthLabel.VEGAN, HealthLabel.VEGETARIAN);
        List<CuisineType> cuisineTypes = List.of(CuisineType.AMERICAN);
        List<MealType> mealTypes = List.of(MealType.BREAKFAST);
        List<DishType> dishTypes = List.of(DishType.PRESERVE);

        Recipe recipe = new Recipe("Seville Orange Marmalade", healthLabels, new BigDecimal("4614.260112"), cuisineTypes, mealTypes, dishTypes,
                List.of("Ingredient 1", "Ingredient 2"));

        String jsonRes = """
                {
                   "_links": {
                     "next": {
                       "href": "https://api.edamam.com/api/recipes/v2?app_key=b8e4f9b364d4901f7d70b2211f95fc92&mealType=Breakfast&_cont=CHcVQBtNNQphDmgVQntAEX4BY0t3AgsDSmxJCmsaalx6DQoORHdcEWsaYVd3UFBWQmJJBmUaZFZxVlIEFmZAAjEUMFcnVwECUQhcETRRPAhhDgEHDg%3D%3D&type=public&app_id=0fd3aae8",
                       "title": "Next page"
                     }
                   },
                   "from": 1,
                   "to": 20,
                   "count": 10000,
                   "hits": [
                     {
                       "recipe": {
                         "label": "Seville Orange Marmalade",
                         "healthLabels": [
                           "Kidney-Friendly",
                           "Vegan",
                           "Vegetarian"
                         ],
                         "totalWeight": 4614.260112,
                         "cuisineType": [
                           "american"
                         ],
                         "mealType": [
                           "breakfast"
                         ],
                         "dishType": [
                           "preserve"
                         ],
                         "ingredientLines": [
                           "Ingredient 1",
                           "Ingredient 2"
                         ]
                       }
                     }
                   ]
                 }
                """;

        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(jsonRes);
        when(mockResponse.statusCode()).thenReturn(200);

        when(clientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        List<Recipe> recipes = recipeSearch.addMealType(MealType.BREAKFAST).query();

        assertFalse(recipes.isEmpty(), "Recipes must not be empty");
        verify(clientMock, times(2)).send(any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class));
        assertTrue(recipes.contains(recipe), "Recipes does not contain the mock recipe");
    }

    @Test
    void queryWithNextTest() throws IOException, InterruptedException, RequestFailedException, MissingParametersException {
        String jsonRes = """
                {
                    "from": 1,
                    "to": 0,
                    "count": 0,
                    "_links": {
                        "next": {
                            "href": "https://api.edamam.com/api/recipes/v2"
                        }
                    },
                    "hits": []
                }
                """;

        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(jsonRes);
        when(mockResponse.statusCode()).thenReturn(200);

        when(clientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        assertDoesNotThrow(() -> recipeSearch.addKeyword("word").query(),
                "Couldn't fetch next page, when it is present");
    }

    @Test
    void queryWithNoNextTest() throws IOException, InterruptedException, RequestFailedException, MissingParametersException {
        String jsonRes = """
                {
                    "from": 1,
                    "to": 0,
                    "count": 0,
                    "_links": {},
                    "hits": []
                }
                """;

        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(jsonRes);
        when(mockResponse.statusCode()).thenReturn(200);

        when(clientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        assertDoesNotThrow(() -> recipeSearch.addHealthLabel(HealthLabel.FISH_FREE).query(),
                "The request should return not throw");
    }

    @Test
    void requestFailsTest() throws IOException, InterruptedException {
        String jsonRes = """
                {
                    "status": "error",
                    "message": "Unauthorized app_id = ********"
                }
                """;

        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(jsonRes);
        when(mockResponse.statusCode()).thenReturn(401);

        when(clientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(RequestFailedException.class,
                () -> recipeSearch.addKeyword("word").query(),
                "Expected query() to throw");

        assertEquals("Unauthorized app_id = ********", exception.getMessage(),
                "Didn't get the expected error message");
    }
}