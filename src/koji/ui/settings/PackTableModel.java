package koji.ui.settings;

import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;
import koji.pack.Pack;
import koji.pack.PacksManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
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
                        return new PackTable.PackIconCellRenderer();
                    }

                    @Override
                    public TableCellEditor getEditor() {
                        return null;
                    }

                    @Override
                    public Class<Icon> getObjectType() {
                        return Icon.class;
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
                        return new PackTable.PackTableCellRenderer();
                    }

                    @Override
                    public TableCellEditor getEditor() {
                        return null;
                    }

                    @Override
                    public Class<Pack> getObjectType() {
                        return Pack.class;
                    }
                });
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

                    @Override
                    public TableCellEditor getEditor() {
                        return new BooleanTableCellEditor();
                    }

                    @Override
                    public Class<Boolean> getObjectType() {
                        return Boolean.class;
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
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).getEditor() != null;
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

        TableCellEditor getEditor();

        Class<T> getObjectType();

    }
}
