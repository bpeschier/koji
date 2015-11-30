package koji;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.FileAttribute;
import com.intellij.util.io.IOUtil;
import com.intellij.util.messages.Topic;
import koji.audio.Channel;
import koji.listeners.KojiChangeListener;
import koji.pack.Pack;
import koji.pack.PacksManager;
import koji.pack.Theme;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class KojiManager implements KojiListener {

    public static Topic<KojiChangeListener> CHANGES = Topic.create("Kōji change", KojiChangeListener.class);

    private boolean enabled = true;
    private PacksManager packsManager;

    private static KojiManager instance;
    private boolean errorState = false;

    private Channel backgroundChannel;
    private Channel foregroundChannel;

    private static final FileAttribute THEME = new FileAttribute("kojiTheme");

    private KojiManager() {
        packsManager = PacksManager.getInstance();

        backgroundChannel = new Channel("background");
        foregroundChannel = new Channel("foreground");
    }

    public static KojiManager getInstance() {
        if (instance == null) {
            instance = new KojiManager();
        }
        return instance;
    }

    public void selectedTheme(Project project, VirtualFile file, Theme theme) {
        setTheme(project, file, theme);

        backgroundChannel.play(theme.getMain());

        project.getMessageBus().syncPublisher(KojiManager.CHANGES).themeChanged(theme);
    }

    public void selectedPack(Project project, VirtualFile virtualFile, Pack pack) {
        setPack(project, virtualFile, pack);

        project.getMessageBus().syncPublisher(KojiManager.CHANGES).packChanged(pack);
        selectedTheme(project, FileEditorManager.getInstance(project).getSelectedFiles()[0], getTheme(project));
    }

    public List<Pack> getPacks() {
        return packsManager.getPacks();
    }


    public Pack getPack(Project project) {
        String packId = PropertiesComponent.getInstance(project).getValue("kojiPack");
        return packsManager.getPack(packId);
    }

    public Pack getPack(Project project, @NotNull VirtualFile virtualFile) {
        Pack pack = null;
        DataInputStream dis = THEME.readAttribute(virtualFile);
        if (dis != null) {
            try {
                String[] ids = IOUtil.readString(dis).split(":");
                pack = packsManager.getPack(ids[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Fallback
        return (pack != null) ? pack : getPack(project);
    }

    public void setPack(Project project, Pack pack) {
        PropertiesComponent.getInstance(project).setValue("kojiPack", pack.getId());
    }

    public void setPack(Project project, VirtualFile virtualFile, Pack pack) {
        // TODO
        setPack(project, pack);
    }

    public Theme getTheme(Project project) {
        String themeId = PropertiesComponent.getInstance(project).getValue("kojiTheme");
        return getPack(project).getTheme(themeId);
    }

    public Theme getTheme(Project project, @NotNull VirtualFile virtualFile) {
        Theme theme = null;

        DataInputStream dis = THEME.readAttribute(virtualFile);
        if (dis != null) {
            try {
                String[] ids = IOUtil.readString(dis).split(":");
                Pack pack = packsManager.getPack(ids[0]);
                theme = pack.getTheme(ids[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Fallback
        return (theme != null) ? theme : getTheme(project);
    }

    public void setTheme(Project project, Theme theme) {
        PropertiesComponent.getInstance(project).setValue("kojiTheme", theme.getId());
    }

    public void setTheme(Project project, VirtualFile virtualFile, Theme theme) {
        // TODO
        setTheme(project, theme);
    }


    //
    // Events
    //

    @Override
    public void projectOpened(Project project) {
        if (!enabled) {
            return;
        }
    }

    @Override
    public void projectClosed(Project project) {
        if (!enabled) {
            return;
        }
    }

    @Override
    public void fileOpened(Project project, VirtualFile file) {

    }

    @Override
    public void fileClosed(Project project, VirtualFile file) {

    }

    @Override
    public void currentFileChanged(Project project, VirtualFile newFile, VirtualFile oldFile) {
        if (!enabled) {
            return;
        }
        System.out.println("New file: " + newFile);
        backgroundChannel.play(getTheme(project, newFile).getMain());
    }

    @Override
    public void windowFocused(Window window) {
        if (!enabled) {
            return;
        }
        System.out.println("Focused on " + window);

        switch (window) {
            case PROJECT_SELECT:
                // TODO
                break;
            case EDITOR:
                if (!errorState) {
                    backgroundChannel.fadeIn();
                    foregroundChannel.fadeOut();
                }
                break;
            case PLUGINS:
                // TODO
                break;
        }

    }

    @Override
    public void compilationDone(Project project, int errors, int warnings) {
        if (!enabled) {
            return;
        }
    }

    @Override
    public void problemsAppeared(Project project, VirtualFile file) {
        errorState = true;
        backgroundChannel.fadeOut();
        foregroundChannel.play(getTheme(project).getWarning());
    }

    @Override
    public void problemsDisappeared(Project project, VirtualFile file) {
        errorState = false;
        backgroundChannel.fadeIn();
        foregroundChannel.stop();
    }

    public boolean isPaused() {
        return !enabled;
    }

    public void resume(Project project) {
        enabled = true;
        backgroundChannel.play(getTheme(project).getMain());
        project.getMessageBus().syncPublisher(KojiManager.CHANGES).isKojiEnabled(true);
    }

    public void pause(Project project) {
        enabled = false;
//        queue.stop();
        project.getMessageBus().syncPublisher(KojiManager.CHANGES).isKojiEnabled(false);
    }

    @Override
    public void applicationExiting() {
        if (!enabled) {
            return;
        }
//        queue.playBlocking(currentPack.getExit());
    }

    public enum Window {
        EDITOR,
        SETTINGS,
        PROJECT_SELECT,
        PLUGINS,
    }
}
