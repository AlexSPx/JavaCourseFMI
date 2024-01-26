package bg.sofia.uni.fmi.mjt.compass.dish;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class DishTypeDeserializer implements JsonDeserializer<DishType> {
    @Override
    public DishType deserialize(JsonElement jsonElement,
                                Type type,
                                JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        return DishType.valueOf(
                jsonElement.getAsString().toUpperCase().replaceAll(" ", "_")
        );
    }
}
