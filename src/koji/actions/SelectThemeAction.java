package koji.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
        System.out.println("Selected " + theme.getName());
        KojiManager.getInstance().selectedTheme(anActionEvent.getProject(), theme);
    }
}
