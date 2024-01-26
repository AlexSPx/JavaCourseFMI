package bg.sofia.uni.fmi.mjt.compass.meal;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListMealTypeDeserializer implements JsonDeserializer<List<MealType>> {
    @Override
    public List<MealType> deserialize(JsonElement jsonElement,
                                      Type type,
                                      JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonArray json = jsonElement.getAsJsonArray();
        List<MealType> mealTypes = new ArrayList<>();

        for (JsonElement mealType : json) {
            if (mealType.getAsString().equals("lunch/dinner")) {
                mealTypes.add(MealType.LUNCH);
                mealTypes.add(MealType.DINNER);
            } else {
                MealType meal = switch (mealType.getAsString()) {
                    case "breakfast" -> MealType.BREAKFAST;
                    case "snack" -> MealType.SNACK;
                    case "teatime" -> MealType.TEATIME;
                    default -> null;
                };

                mealTypes.add(meal);
            }
        }
        return mealTypes;
    }
}
