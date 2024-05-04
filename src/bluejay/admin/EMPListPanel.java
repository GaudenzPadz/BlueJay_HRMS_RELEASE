package bluejay.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import bluejay.Employee;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class EMPListPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public JScrollPane scrollPane;
	public JPanel searchPanel;
	public JTable table;
	private JPopupMenu popupMenu;
	private JMenuItem menuItemRemove, menuItemEdit;
	public final Map<String, String> workTypeMap = new HashMap<>();
	private String[] column = { "ID", "First Name", "Last Name", "Address", "Department", "Work Type", "Basic Salary" };
	private EmployeeDatabase db;
	private DefaultTableModel model = new DefaultTableModel(column, 0) {
		private static final long serialVersionUID = 4L;

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			boolean[] columnEditables = new boolean[] { false, true, true, true, true, true, true };
			return columnEditables[columnIndex];
		}
	};
	private JPanel panel;
	private JButton editBtn;
	private JButton saveBtn;
	private JLabel errorLabel;

	public EMPListPanel(EmployeeDatabase DB) {
		this.db = DB;

		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		TableColumnModel columnModel = table.getColumnModel();

		columnModel.getColumn(0).setPreferredWidth(50);
		columnModel.getColumn(0).setResizable(false);
		columnModel.getColumn(1).setPreferredWidth(100);
		columnModel.getColumn(2).setPreferredWidth(100);
		columnModel.getColumn(3).setPreferredWidth(200);
		columnModel.getColumn(4).setPreferredWidth(200);
		columnModel.getColumn(5).setPreferredWidth(230);
		columnModel.getColumn(6).setPreferredWidth(100);

		table.setToolTipText("Right Click For Options"); // floating text on the table
		table.setCellSelectionEnabled(true);
		table.setFont(new Font("Serif", Font.PLAIN, 18));
		table.setRowHeight(40);

		refreshTable();
		popupMenu();

		searchPanel = new JPanel();
		JTextField searchField = new JTextField(10);
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateTable();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateTable();
			}

			private void updateTable() {
				String searchText = searchField.getText().trim();
				if (searchText.isEmpty()) {
					@SuppressWarnings("unchecked")
					TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
					sorter.setRowFilter(null); // Removes any existing filter
				} else {
					searchTable(searchText); // Perform the search as defined earlier
				}
			}

			private void searchTable(String searchText) {
				TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
				table.setRowSorter(sorter);

				// Filter based on matching case-insensitive cells
				RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter("(?i)" + searchText);
				sorter.setRowFilter(filter);

				if (searchText.trim().length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// I DONT KNOW THE USE OF THIS ONE
			}
		});
		searchPanel.setLayout(new MigLayout("", "[][40px][200.00px][][grow]", "[20px]"));
		searchPanel.add(new JLabel("Search: "), "cell 1 0,alignx left,aligny center");
		searchPanel.add(searchField, "cell 2 0,growx,aligny top");

		JScrollPane scrollPane = new JScrollPane(table);
		setLayout(new BorderLayout(10, 5));
		add(searchPanel, BorderLayout.NORTH);

		errorLabel = new JLabel("");
		errorLabel.setForeground(Color.RED);
		searchPanel.add(errorLabel, "cell 4 0,alignx leading,aligny center");
		add(scrollPane, BorderLayout.CENTER);

		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(panel, BorderLayout.SOUTH);

		ImageIcon saveIcon = new ImageIcon(getClass().getResource("/images/save.png"));
		saveBtn = new JButton(new ImageIcon(saveIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
		saveBtn.setHorizontalAlignment(SwingConstants.LEADING);
		saveBtn.setOpaque(false);
		saveBtn.setContentAreaFilled(false);
		saveBtn.setBorderPainted(false);
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveToDB();

			}
		});
		saveBtn.setEnabled(false);
		panel.add(saveBtn);

		ImageIcon writeIcon = new ImageIcon(getClass().getResource("/images/write.png"));
		editBtn = new JButton(new ImageIcon(writeIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
		editBtn.setHorizontalAlignment(SwingConstants.LEADING);
		editBtn.setOpaque(false);
		editBtn.setContentAreaFilled(false);
		editBtn.setBorderPainted(false);
		editBtn.addActionListener((ActionEvent e) -> {
			editSelected();
		});
		panel.add(editBtn);

		// Add a table model listener to track changes in the table
		model.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				// Enable or disable save button based on modifications
				if (hasModifications()) {
					System.out.println("TRUE");
					saveBtn.setEnabled(true);
				} else {
					System.out.println("FALSE");
					saveBtn.setEnabled(false);
				}
			}

		});
	}

	// Method to check if there are modifications in the table
	private boolean hasModifications() {
		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j < model.getColumnCount(); j++) {
				Object originalValue = table.getValueAt(i, j);
				Object updatedValue = model.getValueAt(i, j);
				if (originalValue == null && updatedValue != null
						|| originalValue != null && !originalValue.equals(updatedValue)) {
					return false; // Found a modification
				}
			}
		}
		return true; // No modifications found
	}

	protected void saveToDB() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			errorLabel.setText("No row selected, select a row to save changes");

			return; // No row selected, nothing to update
		}

		// Retrieve updated information from the table
		int employeeId = (int) table.getValueAt(selectedRow, 0);
		String updatedFirstName = (String) table.getValueAt(selectedRow, 1);
		String updatedLastName = (String) table.getValueAt(selectedRow, 2);
		String updatedAddress = (String) table.getValueAt(selectedRow, 3);
		String updatedDepartment = (String) table.getValueAt(selectedRow, 4);
		String updatedWorkType = (String) table.getValueAt(selectedRow, 4);
		Object value = table.getValueAt(selectedRow, 5);
		double updatedRate;
		if (value instanceof Double) {
			updatedRate = (Double) value;
		} else {
			updatedRate = Double.parseDouble(value.toString());
		}

		// Create an Employee object with the updated information
		Employee updatedEmployee = new Employee(employeeId, updatedFirstName, updatedLastName, updatedAddress,
				updatedDepartment, updatedWorkType, updatedRate);

		saveBtn.setEnabled(false);
		// Update the employee information in the database
		db.updateEmployee(updatedEmployee);
		refreshTable();
	}

	private void popupMenu() {
		// constructs the popup menu
		popupMenu = new JPopupMenu();
		menuItemRemove = new JMenuItem("Remove Current Row");
		menuItemEdit = new JMenuItem("Edit Selected Row");
		popupMenu.add(menuItemRemove);
		popupMenu.add(menuItemEdit);

		menuItemRemove.addActionListener((ActionEvent e) -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(null, "Please select an employee to delete.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Get the employee ID from the selected row
			int employeeId = (int) table.getValueAt(selectedRow, 0);
			String empName = (String) table.getValueAt(selectedRow, 1) + "" + (String) table.getValueAt(selectedRow, 2);

			// Confirm deletion with the user
			int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this employee?",
					"Confirm Deletion", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {

				db.deleteEmployeeData(employeeId, empName);

				JOptionPane.showMessageDialog(null, "Employee deleted successfully.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
				// Refresh table to reflect changes
				refreshTable();
			}
		});

		menuItemEdit.addActionListener((ActionEvent e) -> {
			editSelected();
		});
		// sets the popup menu for the table
		table.setComponentPopupMenu(popupMenu);

	}

	private void editSelected() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(null, "Please select an employee to edit.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Retrieve employee ID from the table
		int employeeId = (int) table.getValueAt(selectedRow, 0);

		try {
			// Fetch complete Employee object from the database

			Employee employee = db.getEmployeeById(employeeId);
			if (employee == null) {
				JOptionPane.showMessageDialog(null, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Display the EmployeeEditWindow
			JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(EMPListPanel.this);
			EmployeeEditWindow editWindow = new EmployeeEditWindow(parentFrame, employee, db);
			editWindow.setVisible(true);
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve employee data: " + ex.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	private void refreshTable() {
		if (db == null) {
			JOptionPane.showMessageDialog(null, "Database connection not established.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return; // Early exit if db is null
		}

		new SwingWorker<Void, Object[]>() {
			@Override
			protected Void doInBackground() throws Exception {
				try (ResultSet rs = db.getAllData()) {
					while (rs.next()) {
						publish(new Object[] { rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"),
								rs.getString("address"), rs.getString("department"), rs.getString("work_type"),
								rs.getDouble("rate") });
					}
				}
				return null;
			}

			@Override
			protected void process(List<Object[]> chunks) {
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.setRowCount(0); // Clear existing rows
				for (Object[] row : chunks) {
					model.addRow(row);
				}
			}

			@Override
			protected void done() {
				try {
					get(); // Handle exceptions during doInBackground
					// calculatePay(); // Update calculations
				} catch (ExecutionException | InterruptedException e) {
					JOptionPane.showMessageDialog(null, "Failed to refresh employee data: " + e.getCause().getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}.execute();
	}

	// Implementing the loading dialog
	private void showLoadingDialog() {
		JDialog loadingDialog = new JDialog();
		JLabel loadingLabel = new JLabel("Calculating pay, please wait...");
		loadingDialog.setLocationRelativeTo(null);
		loadingDialog.setTitle("Loading");
		loadingDialog.getContentPane().add(loadingLabel);
		loadingDialog.pack();
		loadingDialog.setVisible(true);

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				// calculatePay(); // Perform long running calculation
				return null;
			}

			@Override
			protected void done() {
				loadingDialog.dispose(); // Close the dialog
			}
		};
		worker.execute();
	}
}
