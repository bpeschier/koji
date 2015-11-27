package koji.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import koji.KojiManager;

public class PlayPauseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        KojiManager manager = KojiManager.getInstance();
        if (manager.isPaused()) {
            manager.resume(anActionEvent.getProject());
        } else {
            manager.pause(anActionEvent.getProject());
        }
    }
}
