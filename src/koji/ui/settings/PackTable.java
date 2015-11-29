package koji.ui.settings;

import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

public class PackTable extends JBTable {

    public PackTable(PackTableModel model) {
        super(model);


        this.getColumnModel().setColumnMargin(0);

        for (int i = 0; i < model.getColumnCount(); ++i) {
            TableColumn column = this.getColumnModel().getColumn(i);
            PackTableModel.Column columnInfo = model.getColumns().get(i);
            column.setCellEditor(columnInfo.getEditor());
            column.setCellRenderer(columnInfo.getTableCellRenderer());
        }

        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setShowGrid(false);
        this.setTableHeader(null);
    }

    static class PackTableCellRenderer extends DefaultTableCellRenderer {
        protected final JLabel label;

        PackTableCellRenderer() {
            label = new JLabel();
            label.setFont(UIUtil.getLabelFont(UIUtil.FontSize.SMALL));
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        @Override
        protected void setValue(Object value) {
            super.setValue(value);
            label.setText(value.toString());
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component orig = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color bg = orig.getBackground();
            Color grayedFg = isSelected ? orig.getForeground() : JBColor.GRAY;
            label.setForeground(grayedFg);
            label.setBackground(bg);
            label.setOpaque(true);

            return label;
        }
    }

    static class PackIconCellRenderer extends PackTableCellRenderer {

        @Override
        public int getHorizontalAlignment() {
            return CENTER;
        }

        @Override
        public int getVerticalAlignment() {
            return CENTER;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(52, 52);
        }

        @Override
        protected void setValue(Object value) {
            super.setValue(value);
            label.setText("");
            Icon icon = (Icon) value;

            label.setIcon(IconUtil.toSize(IconUtil.scale(icon, 32f / (float) icon.getIconHeight()), 32, 32));
        }

    }

}
