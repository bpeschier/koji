package koji.pack.json.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import koji.pack.Theme;
import koji.pack.json.JsonTheme;

import java.lang.reflect.Type;
import java.net.URL;

public class ThemeDeserializer implements JsonDeserializer<Theme> {

    private URL baseURL;

    public ThemeDeserializer(URL baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public Theme deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return jsonDeserializationContext.deserialize(jsonElement, JsonTheme.class);
    }
}
