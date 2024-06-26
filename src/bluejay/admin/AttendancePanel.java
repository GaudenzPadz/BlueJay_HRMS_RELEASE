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
import javax.swing.table.TableColumnModel;

import bluejay.Employee;
import bluejay.Components.*;
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
						"Gross Pay" },
				0);

		JLabel lblNewLabel = new JLabel("Attendance Monitor");
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		add(lblNewLabel, "cell 0 1");

		attendanceTable = new JTable(attendanceModel);

		TableColumnModel columnModel = attendanceTable.getColumnModel();

		columnModel.getColumn(0).setPreferredWidth(130); // date
		columnModel.getColumn(1).setPreferredWidth(120); // id
		columnModel.getColumn(2).setPreferredWidth(120); // name
		columnModel.getColumn(3).setPreferredWidth(200); // work type
		columnModel.getColumn(4).setPreferredWidth(100); // status
		columnModel.getColumn(5).setPreferredWidth(130); // clock in
		columnModel.getColumn(6).setPreferredWidth(240); // clock in note
		columnModel.getColumn(7).setPreferredWidth(100); // clock out
		columnModel.getColumn(8).setPreferredWidth(200); // clock out note
		columnModel.getColumn(9).setPreferredWidth(200); // GrossPay

		TableActionEvent event = new TableActionEvent() {
			@Override
			public void onEdit(int row) {
				System.out.println("Edit row: " + row);
			}

			@Override
			public void onDelete(int row) {
				System.out.println("Delete row: " + row);
				deleteAttendance();

			}

			@Override
			public void onView(int row) {
				viewAttendanceDetails();
			}
		};

		columnModel.getColumn(10).setCellRenderer(new TableRenderer());
		columnModel.getColumn(10).setCellEditor(new TableEditor(event));
		columnModel.getColumn(10).setResizable(false);

		attendanceTable.setCellSelectionEnabled(true);
		attendanceTable.setFont(new Font("Serif", Font.PLAIN, 18));
		attendanceTable.setRowHeight(40);

		JScrollPane scrollPane = new JScrollPane(attendanceTable);
		add(scrollPane, "cell 0 2,grow");

		JButton deleteButton = new JButton("Delete Attendance");
		deleteButton.addActionListener(e -> deleteAttendance());
		add(deleteButton, "cell 0 3,sizegroupx btn,sizegroupy btn");

		JButton updateButton = new JButton("Update Attendance");
		updateButton.addActionListener(e -> updateAttendance());
		add(updateButton, "cell 0 4,sizegroupx btn,sizegroupy btn");

		JButton viewDetailsButton = new JButton("View Details");
		viewDetailsButton.addActionListener(e -> viewAttendanceDetails());
		add(viewDetailsButton, "cell 0 5,sizegroupx btn,sizegroupy btn");
		loadAttendanceData();
	}

	private void viewAttendanceDetails() {
		int row = attendanceTable.getSelectedRow();
		if (row >= 0) {
			String employeeId = attendanceModel.getValueAt(row, 1).toString(); // Treat as String
			String date = attendanceModel.getValueAt(row, 0).toString();
			String details = db.getAttendanceDetails(employeeId, date);
			JOptionPane.showMessageDialog(this, details, "Attendance Details", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Please select an attendance record to view details", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void updateAttendance() {
		int row = attendanceTable.getSelectedRow();
		if (row >= 0) {
			String employeeId = attendanceModel.getValueAt(row, 1).toString(); // Treat as String
			String date = attendanceModel.getValueAt(row, 0).toString();
			String status = attendanceModel.getValueAt(row, 4).toString();
			String clockInNote = attendanceModel.getValueAt(row, 6).toString();
			String clockOutNote = attendanceModel.getValueAt(row, 8).toString();
			db.updateAttendance(employeeId, date, status, clockInNote, clockOutNote);
			loadAttendanceData();
			JOptionPane.showMessageDialog(this, "Attendance record updated successfully.", "Success",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Please select an attendance record to update", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void deleteAttendance() {
		int row = attendanceTable.getSelectedRow();
		if (row >= 0) {
			String employeeId = attendanceModel.getValueAt(row, 1).toString(); // Treat as String
			String date = attendanceModel.getValueAt(row, 0).toString();

			db.deleteAttendance(employeeId, date);
			JOptionPane.showMessageDialog(this, "Attendance record deleted successfully.", "Success",
					JOptionPane.INFORMATION_MESSAGE);
			loadAttendanceData();

		} else {
			JOptionPane.showMessageDialog(this, "Please select an attendance record to delete", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void loadAttendanceData() {
		attendanceModel.setRowCount(0);
		db.loadAttendanceData(attendanceModel);
	}

}
