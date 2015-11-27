package koji.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import koji.ui.ThemeStatusbarWidget;

public class OpenThemePopupAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("POPUP-TIME!");
        Project currentProject = e.getProject();

        ThemeStatusbarWidget widget = ThemeStatusbarWidget.findWidgetInstance(currentProject);
        if (widget != null)
            widget.showPopupInCenterOf(WindowManager.getInstance().getFrame(currentProject));
    }
}
