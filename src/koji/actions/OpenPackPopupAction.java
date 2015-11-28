package koji.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import koji.ui.PackStatusbarWidget;

public class OpenPackPopupAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project currentProject = e.getProject();

        PackStatusbarWidget widget = PackStatusbarWidget.findWidgetInstance(currentProject);
        if (widget != null)
            widget.showPopupInCenterOf(WindowManager.getInstance().getFrame(currentProject));
    }
}
