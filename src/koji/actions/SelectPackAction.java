package koji.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import koji.KojiManager;
import koji.pack.Pack;
import koji.pack.Theme;

@SuppressWarnings("ComponentNotRegistered")
public class SelectPackAction extends AnAction {

    private Pack pack;

    public SelectPackAction(Pack pack) {
        super(pack.getName());
        this.pack = pack;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        KojiManager.getInstance().selectedPack(anActionEvent.getProject(), pack);
    }
}
