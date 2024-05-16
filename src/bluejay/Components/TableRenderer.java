package bluejay.Components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class TableRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        PanelAction action = new PanelAction();
        action.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : table.getBackground()));
        return action;
    }
}
