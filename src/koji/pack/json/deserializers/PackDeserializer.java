package koji.pack.json.deserializers;

import com.google.gson.*;
import koji.pack.json.JsonPack;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

public class PackDeserializer implements JsonDeserializer<JsonPack> {

    private URL baseURL;

    public PackDeserializer(URL baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public JsonPack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        try {
            obj.addProperty("icon", new URL(baseURL, obj.get("icon").getAsString()).toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return jsonDeserializationContext.deserialize(jsonElement, JsonPack.class);
    }
}
