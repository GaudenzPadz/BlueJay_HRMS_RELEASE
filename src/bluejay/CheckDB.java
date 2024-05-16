package bluejay;

import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;

// Utility class for database connection and table retrieval
class DatabaseUtil {
	private static final String DB_URL = "jdbc:sqlite::resource:DB/bluejayDB.sqlite";

	public static Connection connect() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection(DB_URL);
	}

	public static ResultSet getTableData(Connection connection, String tableName) throws SQLException {
		if (connection == null) {
			throw new SQLException("Connection is null. Establish a connection first.");
		}

		String query = "SELECT * FROM " + tableName; // No user input, so this is safe
		PreparedStatement statement = connection.prepareStatement(query);
		return statement.executeQuery();
	}
}

//Step 1: Allowing cells to be editable
class EditableTableModel extends DefaultTableModel {
	public EditableTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		// Allow all cells to be editable. You could conditionally restrict editing
		// here.
		return true;
	}
}

public class CheckDB extends JFrame {
	private static final long serialVersionUID = 213;
	private Map<String, EditableTableModel> tableModels; // Using EditableTableModel
	private Connection connection;
	private Map<String, JTable> tables; // Map to store JTable references

	public CheckDB() {
		setTitle("Check SQL");
		setSize(1014, 370);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("", "[984px,grow]", "[][461px][grow]"));

		JButton reset = new JButton("CONSOLE");
		reset.addActionListener(e -> this.displayTablesInConsole());

		JButton saveButton = new JButton("SAVE");
		saveButton.addActionListener(e -> saveChanges());

		JButton deleteRowButton = new JButton("DELETE ROW");
		deleteRowButton.addActionListener(e -> deleteSelectedRow());

		getContentPane().add(reset, "flowx,cell 0 0");
		getContentPane().add(saveButton, "cell 0 0");
		getContentPane().add(deleteRowButton, "cell 0 0");

		this.setVisible(true);

		// Initialize database connection
		try {
			connection = DatabaseUtil.connect();
		} catch (SQLException | ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(); // Log the exception
		}

		// Map for storing table models and JTables for easier management
		tableModels = new HashMap<>();
		tables = new HashMap<>();
		String[] tableNames = { "employees", "grossPayTable", "deductions", "department", "attendance", "payroll",
				"users", "types" };
		for (String tableName : tableNames) {
			EditableTableModel model = new EditableTableModel(new Object[][] {}, new Object[] {});
			tableModels.put(tableName, model);

			JTable table = new JTable(model);
			tables.put(tableName, table); // Store the table reference
			JScrollPane sp = new JScrollPane(table);
			getContentPane().add(sp, "cell 0 " + (1 + Arrays.asList(tableNames).indexOf(tableName)) + ",grow");

			// Populate table with data from the database
			try {
				ResultSet resultSet = DatabaseUtil.getTableData(connection, tableName);
				populateTable(resultSet, model);
			} catch (SQLException e) {
				e.printStackTrace(); // Log the exception
			}
		}
	}

	private void deleteSelectedRow() {
		// Example for one table, extend as needed for multiple tables
		String tableName = "employees"; // Change as needed or determine dynamically
		JTable table = tables.get(tableName);
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			try {
				Object primaryKey = table.getValueAt(selectedRow, 0); // Assuming first column is the ID
				String sql = "DELETE FROM " + tableName + " WHERE " + table.getColumnName(0) + " = ?";
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setObject(1, primaryKey);
				preparedStatement.executeUpdate();
				preparedStatement.close();

				// Remove row from the table model
				((DefaultTableModel) table.getModel()).removeRow(selectedRow);
				JOptionPane.showMessageDialog(this, "Row deleted successfully!");
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Error deleting row.", "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this, "No row selected!");
		}
	}

	// Populate the table with the existing data from the database
	private void populateTable(ResultSet resultSet, EditableTableModel model) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();

		// Clear existing columns from the table model
		model.setColumnCount(0);

		// Add columns to the table model
		int columnCount = metaData.getColumnCount();
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			model.addColumn(metaData.getColumnName(columnIndex));
		}

		// Add rows to the table model
		while (resultSet.next()) {
			Object[] rowData = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) {
				rowData[i] = resultSet.getObject(i + 1);
			}
			model.addRow(rowData);
		}

		resultSet.close();
	}

	// Save changes made to the table back to the database
	private void saveChanges() {
		try {
			for (Map.Entry<String, EditableTableModel> entry : tableModels.entrySet()) {

				String tableName = entry.getKey();
				EditableTableModel model = entry.getValue();

				// Construct update statements for each row
				for (int row = 0; row < model.getRowCount(); row++) {
					// Create an UPDATE SQL statement
					StringBuilder updateQuery = new StringBuilder("UPDATE " + tableName + " SET ");

					int columnCount = model.getColumnCount();
					List<Object> params = new ArrayList<>(); // Values to be used in the prepared statement

					// Loop through all columns to construct the set clause
					for (int col = 0; col < columnCount; col++) {
						String columnName = model.getColumnName(col);
						Object value = model.getValueAt(row, col);

						updateQuery.append(columnName).append(" = ?");
						params.add(value);

						if (col < columnCount - 1) {
							updateQuery.append(", ");
						}
					}

					// Assuming the first column is a unique identifier like an ID
					Object primaryKey = model.getValueAt(row, 0); // Get the ID or unique key
					updateQuery.append(" WHERE ").append(model.getColumnName(0)).append(" = ?");
					params.add(primaryKey); // Add ID to the params list for the WHERE clause

					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery.toString());

					// Set parameters for the prepared statement
					for (int i = 0; i < params.size(); i++) {
						preparedStatement.setObject(i + 1, params.get(i));
					}

					preparedStatement.executeUpdate();
					preparedStatement.close();
				}
			}
			JOptionPane.showMessageDialog(this, "Changes saved successfully!");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error saving changes.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(); // Log the exception
		}
	}

	// Console method to print table data
	private void displayTablesInConsole() {
		try {
			String[] tableNames = { "employees", "deductions", "department", "attendance", "payroll", "users",
					"types" };
			for (String tableName : tableNames) {
				System.out.println("Table: " + tableName);
				System.out.println("=".repeat(20));

				ResultSet resultSet = DatabaseUtil.getTableData(connection, tableName);
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();

				// Print column names
				for (int i = 1; i <= columnCount; i++) {
					System.out.print(metaData.getColumnName(i) + "\t");
				}
				System.out.println();

				// Print rows
				while (resultSet.next()) {
					for (int i = 1; i <= columnCount; i++) {
						System.out.print(resultSet.getObject(i) + "\t");
					}
					System.out.println();
				}

				System.out.println("\n");
				resultSet.close();
			}
		} catch (SQLException e) {
			System.err.println("Error displaying table data.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(CheckDB::new);
	}
}
