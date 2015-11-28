package koji.pack;

import org.jetbrains.annotations.NotNull;

public interface Theme {

    @NotNull
    String getId();

    @NotNull
    String getName();

    AudioFile getIntro();
    AudioFile getMain();
    AudioFile getWarning();

}
