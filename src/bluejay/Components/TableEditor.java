package bluejay.Components;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class TableEditor extends DefaultCellEditor {

    private final TableActionEvent event;

    public TableEditor(TableActionEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        PanelAction action = new PanelAction();
        action.initEvent(event, row);
        action.setBackground(table.getSelectionBackground());
        return action;
    }
}