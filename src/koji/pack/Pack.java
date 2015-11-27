package koji.pack;

import javax.swing.*;
import java.util.List;

public interface Pack {

    String getId();

    String getName();

    Icon getIcon();

    List<Theme> getThemes();

    Theme getTheme(String themeId);

    AudioFile getExit();

    AudioFile getMenu();

    AudioFile getPlugins();

}
