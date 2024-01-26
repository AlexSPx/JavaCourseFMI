package bg.sofia.uni.fmi.mjt.compass.health;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class HealthLabelDeserializer implements JsonDeserializer<HealthLabel> {
    @Override
    public HealthLabel deserialize(JsonElement jsonElement,
                                   Type type,
                                   JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        return HealthLabel
                .valueOf(
                        jsonElement.getAsString().toUpperCase().replaceAll("[ -]", "_")
                );
    }
}
