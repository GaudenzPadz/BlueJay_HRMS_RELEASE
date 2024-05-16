package bluejay.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractCellEditor;
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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
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
	private String[] column = { "Employee ID", "First Name", "Last Name", "Department", "Employment Type",
			"Work Type", "Basic Salary", "Actions" };
	private EmployeeDatabase db;
	private DefaultTableModel model = new DefaultTableModel(column, 0) {
		private static final long serialVersionUID = 4L;

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			boolean[] columnEditables = new boolean[] { false, true, true, true, true, true, false, true };
			return columnEditables[columnIndex];
		}
	};

	public EMPListPanel(EmployeeDatabase DB) {
		this.db = DB;
	
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	
		TableColumnModel columnModel = table.getColumnModel();
	
		columnModel.getColumn(0).setPreferredWidth(100); // id
		columnModel.getColumn(0).setResizable(false);
		columnModel.getColumn(1).setPreferredWidth(90); // first name
		columnModel.getColumn(2).setPreferredWidth(100); // last name
		columnModel.getColumn(3).setPreferredWidth(100); // department
		columnModel.getColumn(4).setPreferredWidth(100); // employment type
		columnModel.getColumn(5).setPreferredWidth(200); // work type
		columnModel.getColumn(6).setPreferredWidth(80); // basic salary
		columnModel.getColumn(7).setPreferredWidth(165); // actions
	
		table.getColumnModel().getColumn(7).setCellRenderer(new ActionsRenderer());
		table.getColumnModel().getColumn(7).setCellEditor(new ActionsEditor());
	
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
	
		add(scrollPane, BorderLayout.CENTER);
	}
		// Custom renderer for the "Actions" column
	private class ActionsRenderer extends JPanel implements TableCellRenderer {
		private final JButton editButton = new JButton("Edit");
		private final JButton deleteButton = new JButton("Delete");

		public ActionsRenderer() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			add(editButton);
			add(deleteButton);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	// Custom editor for the "Actions" column
	private class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
		private final JPanel panel = new JPanel();
		private final JButton editButton = new JButton("Edit");
		private final JButton deleteButton = new JButton("Delete");

		public ActionsEditor() {
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
			panel.add(editButton);
			panel.add(deleteButton);

			editButton.addActionListener(e -> {
				fireEditingStopped();
				editSelectedRow(table.getSelectedRow());
			});

			deleteButton.addActionListener(e -> {
				fireEditingStopped();
				deleteSelectedRow(table.getSelectedRow());
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}
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
			String employeeId = (String) table.getValueAt(selectedRow, 0);
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
			editSelectedRow(table.getSelectedRow());
		});
		// sets the popup menu for the table
		table.setComponentPopupMenu(popupMenu);

	}

	// Method to edit the selected row
	private void editSelectedRow(int row) {
		if (row == -1) {
			JOptionPane.showMessageDialog(null, "Please select an employee to edit.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	
		String employeeId = table.getValueAt(row, 0).toString();
		try {
			Employee employee = db.getEmployeeById(employeeId);
			if (employee == null) {
				JOptionPane.showMessageDialog(null, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
	
			JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(EMPListPanel.this);
			EmployeeEditWindow editWindow = new EmployeeEditWindow(parentFrame, employee, db);
			editWindow.setVisible(true);
	
			// Refresh the table after the dialog is closed
			refreshTable();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve employee data: " + e.getMessage(), "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// Method to delete the selected row
	private void deleteSelectedRow(int row) {
		if (row == -1) {
			JOptionPane.showMessageDialog(null, "Please select an employee to delete.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String employeeId = table.getValueAt(row, 0).toString();
		String empName = table.getValueAt(row, 1).toString() + " " + table.getValueAt(row, 2).toString();

		int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this employee?",
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			db.deleteEmployeeData(employeeId, empName);
			JOptionPane.showMessageDialog(null, "Employee deleted successfully.", "Success",
					JOptionPane.INFORMATION_MESSAGE);
			refreshTable();
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
						publish(new Object[] {
								rs.getString("employee_id"), rs.getString("first_name"),
								rs.getString("last_name"), rs.getString("department"),
								rs.getString("employment_type"), rs.getString("work_type"), rs.getDouble("rate") });
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
