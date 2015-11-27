package koji.pack.json.deserializers;

import com.google.gson.*;
import com.intellij.util.Range;
import koji.pack.Theme;
import koji.pack.json.JsonTheme;

import java.lang.reflect.Type;
import java.net.URL;

public class RangeDeserializer implements JsonDeserializer<Range<Float>> {

    @Override
    public Range<Float> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        return new Range<Float>(jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat());
    }
}
