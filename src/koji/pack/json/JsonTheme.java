package koji.pack.json;

import koji.pack.AudioFile;
import koji.pack.Theme;
import org.jetbrains.annotations.NotNull;

public class JsonTheme implements Theme {

    String name;
    AudioFile intro;
    AudioFile main;

    @NotNull
    @Override
    public String getId() {
        return name;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public AudioFile getIntro() {
        return intro;
    }

    @Override
    public AudioFile getMain() {
        return main;
    }

}
