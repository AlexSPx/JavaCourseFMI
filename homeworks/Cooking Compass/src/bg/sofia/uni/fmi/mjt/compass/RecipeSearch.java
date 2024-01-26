package bg.sofia.uni.fmi.mjt.compass;

import bg.sofia.uni.fmi.mjt.compass.dish.DishType;
import bg.sofia.uni.fmi.mjt.compass.dish.DishTypeDeserializer;
import bg.sofia.uni.fmi.mjt.compass.exceptions.MissingParametersException;
import bg.sofia.uni.fmi.mjt.compass.exceptions.RequestFailedException;
import bg.sofia.uni.fmi.mjt.compass.health.HealthLabel;
import bg.sofia.uni.fmi.mjt.compass.health.HealthLabelDeserializer;
import bg.sofia.uni.fmi.mjt.compass.meal.MealType;
import bg.sofia.uni.fmi.mjt.compass.meal.ListMealTypeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class for searching recipes using the Edamam API.
 */
public class RecipeSearch implements RecipeSearchAPI {
    private static final String AUTHORITY = "api.edamam.com";
    private static final String ENDPOINT = "/api/recipes/v2";
    private static final String APPID = "0fd3aae8";
    private static final String APPKEY = "b8e4f9b364d4901f7d70b2211f95fc92";
    private static final int SUCCESS = 200;
    private static final int PAGESPERQUERY = 2;
    private final HttpClient client;
    private final List<String> keywords;
    private final List<HealthLabel> healthLabels;
    private final List<MealType> mealTypes;

    public RecipeSearch() {
        this.client = HttpClient.newHttpClient();
        this.keywords = new ArrayList<>();
        this.healthLabels = new ArrayList<>();
        this.mealTypes = new ArrayList<>();
    }

    public RecipeSearch(HttpClient client) {
        this.client = client;
        this.keywords = new ArrayList<>();
        this.healthLabels = new ArrayList<>();
        this.mealTypes = new ArrayList<>();
    }

    @Override
    public RecipeSearchAPI addKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("Keyword must not be null or empty");
        }

        keywords.add(keyword);
        return this;
    }

    @Override
    public RecipeSearchAPI addHealthLabel(HealthLabel label) {
        if (label == null) {
            throw new IllegalArgumentException("Health label must not be null");
        }

        healthLabels.add(label);
        return this;
    }

    @Override
    public RecipeSearchAPI addMealType(MealType type) {
        if (type == null) {
            throw new IllegalArgumentException("Health label must not be null");
        }

        mealTypes.add(type);
        return this;
    }

    @Override
    public List<Recipe> query() throws MissingParametersException, RequestFailedException {
        if (healthLabels.isEmpty() && keywords.isEmpty() && mealTypes.isEmpty()) {
            throw new MissingParametersException("At least one keyword, mealType or healthLabel must be present");
        }

        try {
            URI uri = new URI("https", AUTHORITY, ENDPOINT, buildParams(), null);

            return fetchData(uri, PAGESPERQUERY);
        } catch (Exception e) {
            throw new RequestFailedException(e.getMessage());
        }
    }

    private List<Recipe> fetchData(URI uri, int pages)
            throws RequestFailedException, IOException, InterruptedException {
        if (pages == 0) return List.of();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .GET().uri(uri).build();
        Gson gson = parser();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == SUCCESS) {
            JsonObject res = gson.fromJson(response.body(), JsonObject.class);
            JsonArray hits = res.getAsJsonArray("hits");
            JsonObject links = res.getAsJsonObject("_links");
            List<Recipe> recipes = hits.asList().stream()
                    .map(hitElement -> hitElement.getAsJsonObject().getAsJsonObject("recipe"))
                    .map(recipeObject -> gson.fromJson(recipeObject, Recipe.class))
                    .collect(Collectors.toList());

            if (!links.has("next")) {
                return recipes;
            }

            URI next = URI.create(links.getAsJsonObject("next").get("href").getAsString());
            recipes.addAll(fetchData(next, pages - 1));
            return recipes;
        } else {
            throw new RequestFailedException(gson.fromJson(response.body(), JsonObject.class)
                            .getAsJsonPrimitive("message").getAsString());
        }
    }

    private Gson parser() {
        return new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<MealType>>() {
                }.getType(), new ListMealTypeDeserializer())
                .registerTypeAdapter(HealthLabel.class, new HealthLabelDeserializer())
                .registerTypeAdapter(DishType.class, new DishTypeDeserializer())
                .create();
    }

    private String buildParams() {
        StringBuilder params =
                new StringBuilder("type=public&app_id=").append(APPID).append("&app_key=").append(APPKEY);

        if (!healthLabels.isEmpty()) {
            String joinedHealthLabels = healthLabels.stream()
                    .map(HealthLabel::getValue)
                    .collect(Collectors.joining("&health=", "&health=", ""));

            params.append(joinedHealthLabels);
        }
        if (!mealTypes.isEmpty()) {
            String joinedMealTypes = mealTypes.stream()
                    .map(MealType::getValue)
                    .collect(Collectors.joining("&mealType=", "&mealType=", ""));

            params.append(joinedMealTypes);
        }

        if (!keywords.isEmpty()) {
            String joinedKeywords = keywords.stream()
                    .collect(Collectors.joining(",", "&q=", ""));

            params.append(joinedKeywords);
        }

        return params.toString();
    }
}
