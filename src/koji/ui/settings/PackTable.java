package koji.ui.settings;

import com.intellij.ui.table.JBTable;

import javax.swing.table.TableCellRenderer;

public class PackTable extends JBTable {

    private PackTableModel model;

    public PackTable(PackTableModel model) {
        super(model);
        this.model = model;


    }

    @Override
    public TableCellRenderer getCellRenderer(int rowIndex, int columnIndex) {
        return model.getColumns().get(columnIndex).getTableCellRenderer();
    }
}
