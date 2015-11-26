package koji.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import koji.pack.Theme;
import org.jetbrains.annotations.NotNull;

public class ThemePopup {

    public static ThemePopup getInstance(@NotNull Project project, @NotNull Theme theme) {
        return new ThemePopup(project, theme);
    }

    public ListPopup asListPopup() {
        return null;
    }

    private ThemePopup(Project project, Theme theme) {

    }

}
