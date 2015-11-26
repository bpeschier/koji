package koji.pack.json.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import koji.pack.json.JsonPack;

import java.lang.reflect.Type;
import java.net.URL;

public class PackDeserializer implements JsonDeserializer<JsonPack> {

    private URL baseURL;

    public PackDeserializer(URL baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public JsonPack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return jsonDeserializationContext.deserialize(jsonElement, JsonPack.class);
    }
}
