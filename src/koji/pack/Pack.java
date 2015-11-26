package koji.pack;

import java.util.List;

public interface Pack {

    List<Theme> getThemes();

    AudioFile getExit();

    AudioFile getMenu();

    Theme getCurrentTheme();

}
