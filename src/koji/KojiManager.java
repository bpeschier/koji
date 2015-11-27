package koji;

import com.intellij.openapi.project.Project;
import koji.audio.Queue;
import koji.pack.Pack;
import koji.pack.PacksManager;
import koji.pack.Theme;

import java.util.HashMap;
import java.util.Map;

public class KojiManager implements KojiListener {

    private boolean enabled = true;
    private Pack pack;
    private Queue queue;
    private Map<Project, Pack> projectPacks = new HashMap<Project, Pack>();
    private PacksManager packsManager;

    public KojiManager(PacksManager manager) {
        queue = new Queue();
        packsManager = manager;

        pack = packsManager.getPacks().get(0);
    }

    public void usePack(Pack pack) {
        this.pack = pack;

        update();
    }

    private void update() {

    }

    private Pack getProjectPack(Project project) {
        Pack p = projectPacks.get(project);
        return (p == null) ? pack : p;
    }

    @Override
    public void projectOpened(Project project) {
        if (!enabled) {
            return;
        }
        Pack pack = getProjectPack(project);
        Theme theme = pack.getCurrentTheme();
        queue.playTheme(theme);
    }

    @Override
    public void projectClosed(Project project) {
        if (!enabled) {
            return;
        }
    }

    @Override
    public void projectSwitched(Project from, Project to) {
        if (!enabled) {
            return;
        }
        projectOpened(to);
    }

    @Override
    public void windowFocused(Window window) {
        if (!enabled) {
            return;
        }
        System.out.println("Focused on " + window);

        switch (window) {
            case PROJECT_SELECT:
                queue.playBackground(pack.getMenu());
                break;
            case EDITOR:
                queue.resumeBackground();
                queue.stopForeground();
                break;
            case PLUGINS:
                queue.pauseBackground();
                queue.playForeground(pack.getPlugins());
                break;
        }

    }

    @Override
    public void applicationExiting() {
        if (!enabled) {
            return;
        }
        queue.playBlocking(pack.getExit());
    }

    public enum Window {
        EDITOR,
        SETTINGS,
        PROJECT_SELECT,
        PLUGINS,
    }
}
