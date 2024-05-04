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

public class CheckDB extends JFrame {
	private static final long serialVersionUID = 213;
	private Map<String, DefaultTableModel> tableModels;
	private Connection connection;

	public CheckDB() {
		setTitle("Check SQL");
		setSize(1014, 370);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("", "[984px,grow]", "[][461px][grow]"));

		JButton reset = new JButton("Reset Tables");
		reset.addActionListener(e -> resetTables());

		getContentPane().add(reset, "flowx,cell 0 0");
		this.setVisible(true);
		// Initialize database connection
		try {
			connection = DatabaseUtil.connect();
		} catch (SQLException | ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(); // Log the exception
		}

		// Map for storing table models for easier management
		tableModels = new HashMap<>();
		String[] tableNames = { "employees", "deductions" , "department","attendance", "payroll", "users", "types" };
		for (int i = 0; i < tableNames.length; i++) {
			DefaultTableModel model = new DefaultTableModel();
			tableModels.put(tableNames[i], model);

			JTable table = new JTable(model);
			JScrollPane sp = new JScrollPane(table);
			getContentPane().add(sp, "cell 0 " + i + ",grow");

			// Populate table with data from the database
			try {
				ResultSet resultSet = DatabaseUtil.getTableData(connection, tableNames[i]);
				populateTable(resultSet, model);
			} catch (SQLException e) {
				e.printStackTrace(); // Log the exception
			}
		}
	}

	private void populateTable(ResultSet resultSet, DefaultTableModel model) throws SQLException {
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

	private void resetTables() {
//		// Confirm the reset action with the user
//		int confirm = JOptionPane.showConfirmDialog(this,
//				"Are you sure you want to reset all data in the database? This action cannot be undone.",
//				"Confirm Reset", JOptionPane.YES_NO_OPTION);
//
//		if (confirm == JOptionPane.YES_OPTION) {
//			String[] tableNames = { "employees", "attendance", "users", "types" };
//			try {
//				for (String tableName : tableNames) {
//					String resetQuery = "DELETE FROM " + tableName;
//					PreparedStatement statement = connection.prepareStatement(resetQuery);
//					statement.executeUpdate(); // Delete all rows from the table
//				}
//
//				// Reload data in the tables
//				for (String tableName : tableNames) {
//					DefaultTableModel model = tableModels.get(tableName);
//					ResultSet resultSet = DatabaseUtil.getTableData(connection, tableName);
//					populateTable(resultSet, model);
//				}
//
//				JOptionPane.showMessageDialog(this, "Tables reset successfully.");
//			} catch (SQLException e) {
//				JOptionPane.showMessageDialog(this, "Error resetting tables.", "Error", JOptionPane.ERROR_MESSAGE);
//				e.printStackTrace();
//			}
//		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(CheckDB::new);
	}

}
