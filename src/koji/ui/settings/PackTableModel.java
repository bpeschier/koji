package koji.ui.settings;

import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.util.ui.table.IconTableCellRenderer;
import koji.pack.Pack;
import koji.pack.PacksManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.List;

public class PackTableModel extends AbstractTableModel {

    private List<Pack> packs;
    private List<Column> columns;
    private PacksManager manager;

    public PackTableModel(final PacksManager manager) {
        this.manager = manager;
        this.packs = manager.getPacks();
        this.columns = new ArrayList<Column>() {
            {
                add(new Column<Boolean>() {
                    @Override
                    public String getTitle() {
                        return "Enabled";
                    }

                    @Override
                    public Boolean getObject(Pack pack) {
                        return manager.isEnabled(pack);
                    }

                    @Override
                    public TableCellRenderer getTableCellRenderer() {
                        return new BooleanTableCellRenderer(SwingConstants.CENTER);
                    }
                });
                add(new Column<Icon>() {
                    @Override
                    public String getTitle() {
                        return null;
                    }

                    @Override
                    public Icon getObject(Pack pack) {
                        return pack.getIcon();
                    }

                    @Override
                    public TableCellRenderer getTableCellRenderer() {
                        return new IconTableCellRenderer<Icon>() {
                            @Nullable
                            @Override
                            protected Icon getIcon(@NotNull Icon icon, JTable jTable, int i) {
                                return icon;
                            }
                        };
                    }
                });
                add(new Column<Pack>() {
                    @Override
                    public String getTitle() {
                        return "Pack";
                    }

                    @Override
                    public Pack getObject(Pack pack) {
                        return pack;
                    }

                    @Override
                    public TableCellRenderer getTableCellRenderer() {
                        return new DefaultTableCellRenderer();
                    }
                });
            }
        };
    }

    @Override
    public String getColumnName(int idx) {
        return columns.get(idx).getTitle();
    }

    @Override
    public int getRowCount() {
        return packs.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Column<?> column = columns.get(columnIndex);
        Pack pack = packs.get(rowIndex);
        return column.getObject(pack);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public interface Column<T> {
        String getTitle();

        T getObject(Pack pack);

        TableCellRenderer getTableCellRenderer();
    }
}
