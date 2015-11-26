package koji.pack.json.deserializers;

import com.google.gson.*;
import koji.pack.AudioFile;
import koji.pack.json.JsonAudioFile;
import koji.pack.json.JsonPack;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

public class AudioFileDeserializer implements JsonDeserializer<AudioFile> {

    private URL baseURL;

    public AudioFileDeserializer(URL baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public AudioFile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        try {
            obj.addProperty("path", new URL(baseURL, obj.get("path").getAsString()).toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return jsonDeserializationContext.deserialize(jsonElement, JsonAudioFile.class);
    }
}
