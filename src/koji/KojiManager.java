package koji;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import koji.audio.Queue;
import koji.listeners.EnabledChangeListener;
import koji.listeners.ThemeChangeListener;
import koji.pack.Pack;
import koji.pack.PacksManager;
import koji.pack.Theme;

public class KojiManager implements KojiListener {

    public static Topic<ThemeChangeListener> THEME_CHANGE = Topic.create("Theme change", ThemeChangeListener.class);
    public static Topic<EnabledChangeListener> ENABLE_CHANGE = Topic.create("Enabled change", EnabledChangeListener.class);

    private boolean enabled = true;
    private Pack pack;
    private Queue queue;
    private PacksManager packsManager;

    private static KojiManager instance;

    private KojiManager() {
        queue = new Queue();
        packsManager = PacksManager.getInstance();

        pack = packsManager.getPacks().get(0);
    }

    public static KojiManager getInstance() {
        if (instance == null) {
            instance = new KojiManager();
        }
        return instance;
    }

    public void usePack(Pack pack) {
        this.pack = pack;

        update();
    }

    public void selectedTheme(Project project, Theme theme) {
        queue.playTheme(theme);
        PropertiesComponent.getInstance(project).setValue("kojiCurrentTheme", theme.getName());
        project.getMessageBus().syncPublisher(KojiManager.THEME_CHANGE).themeChanged(theme);
    }

    private void update() {

    }


    public Pack getProjectPack(Project project) {
        String packId = PropertiesComponent.getInstance(project).getValue("kojiPack");
        return packsManager.getPack(packId);
    }

    public Theme getCurrentProjectTheme(Project project) {
        String themeId = PropertiesComponent.getInstance(project).getValue("kojiCurrentTheme");
        return getProjectPack(project).getTheme(themeId);
    }

    //
    // Events
    //

    @Override
    public void projectOpened(Project project) {
        if (!enabled) {
            return;
        }
        queue.playTheme(getCurrentProjectTheme(project));
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

    public boolean isPaused() {
        return !enabled;
    }

    public void resume(Project project) {
        enabled = true;
        queue.playTheme(getCurrentProjectTheme(project));
        project.getMessageBus().syncPublisher(KojiManager.ENABLE_CHANGE).isKojiEnabled(true);
    }

    public void pause(Project project) {
        enabled = false;
        queue.stop();
        project.getMessageBus().syncPublisher(KojiManager.ENABLE_CHANGE).isKojiEnabled(false);
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
