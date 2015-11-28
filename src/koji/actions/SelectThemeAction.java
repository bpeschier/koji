package koji.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import koji.KojiManager;
import koji.pack.Theme;

@SuppressWarnings("ComponentNotRegistered")
public class SelectThemeAction extends AnAction {

    private Theme theme;

    public SelectThemeAction(Theme theme) {
        super(theme.getName());
        this.theme = theme;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        KojiManager.getInstance().selectedTheme(anActionEvent.getProject(), anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE), theme);
    }
}
