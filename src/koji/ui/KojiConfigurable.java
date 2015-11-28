package koji.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBoxTableRenderer;
import com.intellij.openapi.ui.StripeTable;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.table.IconTableCellRenderer;
import koji.pack.Pack;
import koji.pack.PacksManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.util.*;
import java.awt.*;
import java.util.List;

public class KojiConfigurable implements Configurable {
    @NotNull
    private final KojiConfigurable.Model packsTableModel = new KojiConfigurable.Model();
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

    private static final class KojiSettingsPanel extends JPanel {
        @NotNull
        private final PackTable packTable;

        public KojiSettingsPanel(Model model) {
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

    private static final class PackTable extends StripeTable {

        public PackTable(@NotNull Model model) {
            super(model);
            getTableColumn(Column.ICON).setPreferredWidth(50);
            getTableColumn(Column.NAME).setPreferredWidth(400);
            final TableColumn iconColumn = getTableColumn(Column.ICON);
            IconTableCellRenderer<Pack> iconRenderer = new IconTableCellRenderer<Pack>() {
                @Nullable
                @Override
                protected Icon getIcon(@NotNull Pack pack, JTable jTable, int i) {
                    return pack.getIcon();
                }

                @Override
                public String getText() {
                    return "";
                }

            };
            iconColumn.setPreferredWidth(50);
            iconColumn.setCellRenderer(iconRenderer);
        }

        @NotNull
        @Override
        public Dimension getMinimumSize() {
            return calcSize(super.getMinimumSize());
        }

        @NotNull
        @Override
        public Dimension getPreferredSize() {
            return calcSize(super.getPreferredSize());
        }

        @NotNull
        private Dimension calcSize(@NotNull Dimension dimension) {
            final Container container = getParent();
            if (container != null) {
                final Dimension size = container.getSize();
                return new Dimension(size.width, dimension.height);
            }
            return dimension;
        }

        @NotNull
        private TableColumn getTableColumn(@NotNull Column column) {
            return getColumnModel().getColumn(column.getIndex());
        }

    }

    private static final class Model extends AbstractTableModel {
        @NotNull
        private final List<Row> myRows = new ArrayList<Row>();

        public Model() {
            reset();
        }

        @Override
        public int getRowCount() {
            return myRows.size();
        }

        @Override
        public int getColumnCount() {
            return Column.values().length;
        }

        @Nullable
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final Column column = Column.fromIndex(columnIndex);
            if (column != null && rowIndex >= 0 && rowIndex < myRows.size()) {
                final Row row = myRows.get(rowIndex);
                return row.getPack();
            }
            return null;
        }

        @Override
        public void setValueAt(Object object, int rowIndex, int columnIndex) {
            final Column column = Column.fromIndex(columnIndex);
            if (column != null && rowIndex >= 0 && rowIndex < myRows.size() && object instanceof Pack) {
                final Row row = myRows.get(rowIndex);
                row.setPack((Pack) object);
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Nullable
        @Override
        public String getColumnName(int index) {
            final Column column = Column.fromIndex(index);
            return column != null ? column.getTitle() : null;
        }

        public void reset() {
            myRows.clear();
            for (Pack pack : PacksManager.getInstance().getPacks()) {
                myRows.add(new Row(pack));
            }
            Collections.sort(myRows);
        }

    }

    private static final class Row implements Comparable<Row> {
        @NotNull
        private Pack pack;

        private Row(@NotNull Pack pack) {
            this.pack = pack;
        }

        @NotNull
        public Pack getPack() {
            return pack;
        }

        @Override
        public int compareTo(@NotNull Row row) {
            return getPack().getId().compareTo(row.getPack().getId());
        }

        public void setPack(@NotNull Pack pack) {
            this.pack = pack;
        }
    }

    private enum Column {
        ICON(0, ""),
        NAME(1, "Name");

        @NotNull
        private static final Map<Integer, Column> ourMembers = new HashMap<Integer, Column>();

        static {
            for (Column column : values()) {
                ourMembers.put(column.myIndex, column);
            }
        }

        private final int myIndex;
        @NotNull
        private final String myTitle;

        Column(int index, @NotNull String title) {
            myIndex = index;
            myTitle = title;
        }

        @Nullable
        public static Column fromIndex(int index) {
            return ourMembers.get(index);
        }

        public int getIndex() {
            return myIndex;
        }

        @NotNull
        public String getTitle() {
            return myTitle;
        }
    }
}