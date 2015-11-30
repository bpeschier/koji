package koji;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ApplicationComponent;
import koji.listeners.ApplicationListener;
import koji.pack.PacksManager;
import org.jetbrains.annotations.NotNull;

public class KojiComponent implements ApplicationComponent {

    KojiManager manager;
    PacksManager packsManager;

    @Override
    public void initComponent() {
        String homeDir = System.getProperty("user.home");
        PropertiesComponent.getInstance().setValues("kojiPackPaths", new String[]{
                String.format("file://%s/Downloads/koji/WindWaker.koji/", homeDir),
                String.format("file://%s/Downloads/koji/Mario64.koji/", homeDir),
                String.format("file://%s/Downloads/koji/MonkeyIsland.koji/", homeDir)
        });
        packsManager = PacksManager.getInstance();
        manager = KojiManager.getInstance();
        ApplicationListener.install(manager);

    }

    @Override
    public void disposeComponent() {
        ApplicationListener.uninstall();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getName();
    }

}