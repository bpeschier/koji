package koji;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import koji.ui.ThemeStatusbarWidget;
import org.jetbrains.annotations.NotNull;

public class KojiProjectComponent implements ProjectComponent {

    private Project project;
    private ThemeStatusbarWidget widget;

    public KojiProjectComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {

        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar != null) {
            widget = new ThemeStatusbarWidget(project);
            statusBar.addWidget(widget, "after " + ThemeStatusbarWidget.class.getName(), project);
            statusBar.updateWidget(widget.getClass().getName());
        }
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getName();
    }
}
