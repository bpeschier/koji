package koji.ui.settings;

import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public final class KojiSettingsPanel extends JPanel {
    @NotNull
    private final PackTable packTable;

    public KojiSettingsPanel(PackTableModel model) {
        packTable = new PackTable(model);
        setLayout(new BorderLayout());
        final JScrollPane scrollPane = new JBScrollPane(packTable);
        scrollPane.setBorder(new LineBorder(UIUtil.getBorderColor()));
        final JPanel conflictsPanel = new JPanel(new BorderLayout());
        final String title = "Installed packs";
        conflictsPanel.setBorder(IdeBorderFactory.createTitledBorder(title, false));
        conflictsPanel.add(scrollPane);
        add(conflictsPanel, BorderLayout.CENTER);
    }
}
