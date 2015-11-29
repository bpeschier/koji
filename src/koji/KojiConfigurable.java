package koji;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import koji.pack.PacksManager;
import koji.ui.settings.KojiSettingsPanel;
import koji.ui.settings.PackTableModel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class KojiConfigurable implements Configurable {
    @NotNull
    private final PackTableModel packsTableModel = new PackTableModel(PacksManager.getInstance());
    @NotNull
    private final KojiSettingsPanel myPanel = new KojiSettingsPanel(packsTableModel);

    @Nls
    @Override
    public String getDisplayName() {
        return "Kōji";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return myPanel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}