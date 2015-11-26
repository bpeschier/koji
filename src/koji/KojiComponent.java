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
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        PropertiesComponent.getInstance().setValues("kojiPackPaths", new String[]{
                "file:///Users/bpeschier/Downloads/koji/WindWaker.koji/"
        });
        packsManager = new PacksManager(this);
        manager = new KojiManager(packsManager);
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
