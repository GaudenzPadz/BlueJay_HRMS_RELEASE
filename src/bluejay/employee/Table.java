package bluejay.employee;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class Table {
    public static void main(String[] args) {
        // Create JFrame
        JFrame frame = new JFrame("Attendance Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create JPanel to hold the summary
        JPanel summaryPanel = new JPanel();
        JLabel summaryLabel = new JLabel("Gross Pay: $0.00");
        summaryPanel.add(summaryLabel);

        // Create JTable
        DefaultTableModel model = new DefaultTableModel(15, 1); // 15 rows, 1 column
        JTable table = new JTable(model);

        // Populate first 15 rows with random values
        for (int row = 0; row < 15; row++) {
            int value = (int) (Math.random() * 100); // Random value between 0 and 99
            model.setValueAt(value, row, 0);
        }

        // Calculate sum of first 15 rows
        int sum = 0;
        for (int row = 0; row < 15; row++) {
            sum += (int) model.getValueAt(row, 0);
        }

        // Set sum value in the summary label
        summaryLabel.setText("Gross Pay: $" + sum);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnModel columnModel = table.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(200);// name

        table.setFont(new Font("Serif", Font.PLAIN, 18));
        table.setRowHeight(40);
        // Create JScrollPane for the JTable
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Set preferred size for summary panel based on row height of the table
        summaryPanel.setPreferredSize(new Dimension(frame.getWidth(), table.getRowHeight()));


        // Create JSplitPane to hold the summary panel and the table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, summaryPanel);

        // Set the divider location of the split pane to match the height of the rows in the table
        splitPane.setDividerLocation(350);
        
        // Add split pane to frame
        frame.add(splitPane);

        // Set JFrame properties
        frame.setSize(150, 400);
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }
}
