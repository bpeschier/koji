package koji.pack.json;

import com.google.gson.GsonBuilder;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.Range;
import koji.pack.AudioFile;
import koji.pack.Pack;
import koji.pack.Theme;
import koji.pack.json.deserializers.AudioFileDeserializer;
import koji.pack.json.deserializers.PackDeserializer;
import koji.pack.json.deserializers.RangeDeserializer;
import koji.pack.json.deserializers.ThemeDeserializer;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class JsonPack implements Pack {

    URL icon;
    String name;
    List<Theme> themes;
    AudioFile exit;
    AudioFile menu;
    AudioFile plugins;

    public static Pack load(URL url) {
        try {
            URL manifest = new URL(url, "manifest.json");
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Pack.class, new PackDeserializer(url));
            builder.registerTypeAdapter(Range.class, new RangeDeserializer());
            builder.registerTypeAdapter(AudioFile.class, new AudioFileDeserializer(url));
            builder.registerTypeAdapter(Theme.class, new ThemeDeserializer(url));
            return builder.create().fromJson(new InputStreamReader(manifest.openStream()), Pack.class);
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException ioe) {
            return null;
        }
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Theme> getThemes() {
        return themes;
    }

    @Override
    public Theme getTheme(String themeId) {
        for (Theme t : getThemes()) {
            if (t.getId().equals(themeId)) {
                return t;
            }
        }
        return getThemes().get(0);
    }

    @Override
    public AudioFile getExit() {
        return exit;
    }

    @Override
    public AudioFile getMenu() {
        return menu;
    }

    @Override
    public AudioFile getPlugins() {
        return plugins;
    }

    public Icon getIcon() {
        return IconLoader.findIcon(icon);
    }

}
