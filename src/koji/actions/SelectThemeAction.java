package koji.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import koji.KojiManager;
import koji.pack.Theme;

public class SelectThemeAction extends AnAction {

    private Theme theme;
    private Project project;

    public SelectThemeAction(Project project, Theme theme) {
        super(theme.getName());
        this.theme = theme;
        this.project = project;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        System.out.println("Selected " + theme.getName());
        KojiManager.getInstance().selectedTheme(project, theme);
    }
}
