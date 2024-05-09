package bluejay.admin;

import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import bluejay.Employee;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class AttendancePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private DefaultTableModel attendanceModel;
    private EmployeeDatabase db;
    private List<Employee> employees;
    private JTable attendanceTable;

    public AttendancePanel(EmployeeDatabase db) {
        this.db = db;
        setLayout(new MigLayout("wrap, fillx, insets 25 35 25 35", "[grow]", "[][][][grow][]"));

        attendanceModel = new DefaultTableModel(
                new String[] { "Date", 
                			   "ID", 
                			   "Name", 
                			   "Work Type",
                			   "Status",
                			   "Clock In", 
                			   "Clock In Note", 
                			   "Clock Out", 
                			   "Clock Out Note", 
                			   "Overtime",
                			   "Gross Pay" }, 0);

        JLabel lblNewLabel = new JLabel("Attendance Monitor");
        lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lblNewLabel, "cell 0 1");

        attendanceTable = new JTable(attendanceModel);
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        add(scrollPane, "cell 0 2,grow");

        JButton deleteButton = new JButton("Delete Attendance");
        deleteButton.addActionListener(e -> deleteAttendance());
        add(deleteButton, "cell 0 3,sizegroupx btn,sizegroupy btn");
        loadAttendanceData();
    }

    private void loadAttendanceData() {
        attendanceModel.setRowCount(0);
        db.loadAttendanceData(attendanceModel);
    }

    private void deleteAttendance() {
        int row = attendanceTable.getSelectedRow();
        if (row >= 0) {
            int employeeId = Integer.parseInt(attendanceModel.getValueAt(row, 1).toString());
            String date = attendanceModel.getValueAt(row, 0).toString();
            
                db.deleteAttendance(employeeId, date);
                loadAttendanceData();
                JOptionPane.showMessageDialog(this, "Attendance record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
          
        } else {
            JOptionPane.showMessageDialog(this, "Please select an attendance record to delete", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

}
