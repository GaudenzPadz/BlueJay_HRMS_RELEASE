package bluejayDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import bluejay.Employee;

public class EmployeeDatabase {
	private Connection connection;

	public EmployeeDatabase() throws SQLException, ClassNotFoundException {
		connect();
	}

	public void connect() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		// Ensure connection is closed if previously opened
		if (this.connection != null) {
			this.connection.close();
		}
		this.connection = DriverManager.getConnection("jdbc:sqlite::resource:DB/bluejayDB.sqlite");
		if (this.connection == null) {
			JOptionPane.showMessageDialog(null, "Failed to connect to Database", "Error", JOptionPane.ERROR_MESSAGE);
			throw new SQLException("Failed to establish connection to the database.");
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public String validateLogin(String username, String password) {
		try {
			String sql = "SELECT name, role FROM users WHERE username = ? AND password = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				String name = rs.getString("name");
				String role = rs.getString("role");
				return "Login successful! Welcome " + name + " (Role: " + role + ")";
			} else {
				return "Invalid username or password.";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return "Database error during login.";
		}
	}

	public Employee getEmployeeDataByUsername(String username) {
		try {
			String sql = "SELECT e.*, t.work_type,  d.department_name AS department FROM employees e "
					+ "LEFT JOIN types t ON e.work_type_id = t.id "
					+ "LEFT JOIN department d ON e.department_id = d.department_id "
					+ "WHERE e.email = (SELECT email FROM users WHERE username = ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				Employee employee = new Employee();
				employee.setId(rs.getInt("id"));
				employee.setFirstName(rs.getString("first_name"));
				employee.setMiddleName(rs.getString("middle_name"));
				employee.setLastName(rs.getString("last_name"));
				employee.setAddress(rs.getString("address"));
				employee.setDepartment(rs.getString("department")); // Ensure this is correct
				employee.setWorkType(rs.getString("work_type"));
				employee.setBasicSalary(rs.getDouble("rate"));
				employee.setGrossPay(rs.getDouble("grossPay"));
				employee.setNetPay(rs.getDouble("netPay"));
				employee.setGender(rs.getString("gender"));
				employee.setTelNUmber(rs.getString("tel_number"));
				employee.setEmail(rs.getString("email"));
				byte[] imageData = rs.getBytes("profile_image");
				if (imageData != null) {
					employee.setProfileImage(imageData);
				} else {
					employee.setProfileImage(null); // Handle null image data
				}

				// converts unix timestamp into a java.sql.Date

				employee.setDateHired(Date.valueOf(Instant.ofEpochSecond(rs.getInt("date_hired"))
						.atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalDate()));

				employee.setDOB(Date.valueOf(Instant.ofEpochSecond(rs.getInt("DOB")).atZone(ZoneId.systemDefault())
						.toLocalDateTime().toLocalDate()));

				return employee;
			} else {
				return null; // No employee data found for this username
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null; // Handle SQL error
		}
	}

	public List<Employee> getAllEmployees() {
		List<Employee> employees = new ArrayList<>();
		String sql = "SELECT e.id, e.first_name, e.middle_name, e.last_name, e.address, d.department_name AS department, "
				+ "t.work_type, e.rate, e.grossPay, e.netPay, e.gender, e.tel_Number, e.email, e.profile_image, e.date_hired, e.DOB "
				+ "FROM employees e " + "LEFT JOIN types t ON e.work_type_id = t.id "
				+ "LEFT JOIN department d ON e.department_id = d.department_id";
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				Employee employee = new Employee();
				employee.setId(rs.getInt("id"));
				employee.setFirstName(rs.getString("first_name"));
				employee.setMiddleName(rs.getString("middle_name"));
				employee.setLastName(rs.getString("last_name"));
				employee.setAddress(rs.getString("address"));
				employee.setDepartment(rs.getString("department")); // Ensure this is correct
				employee.setWorkType(rs.getString("work_type"));
				employee.setBasicSalary(rs.getDouble("rate"));
				employee.setGrossPay(rs.getDouble("grossPay"));
				employee.setNetPay(rs.getDouble("netPay"));
				employee.setGender(rs.getString("gender"));
				employee.setTelNUmber(rs.getString("tel_number"));
				employee.setEmail(rs.getString("email"));
				byte[] imageData = rs.getBytes("profile_image");
				if (imageData != null) {
					employee.setProfileImage(imageData);
				} else {
					employee.setProfileImage(null); // Handle null image data
				}
				// converts unix timestamp into a java.sql.Date
				employee.setDateHired(Date.valueOf(Instant.ofEpochSecond(rs.getLong("date_hired"))
						.atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalDate()));

				employee.setDOB(Date.valueOf(Instant.ofEpochSecond(rs.getLong("DOB")).atZone(ZoneId.systemDefault())
						.toLocalDateTime().toLocalDate()));

				employees.add(employee);
			}
			rs.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error Getting All Employees Data " + e.getMessage(),
					"Initialization Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		return employees;
	}

	/**
	 * Counts the number of new employees hired today.
	 *
	 * @return the count of new employees hired today
	 */
	public int countNewEmployeesToday() {
		try {
			// Get the current date
			LocalDate today = LocalDate.now();
			Date sqlDateToday = Date.valueOf(today);

			// Prepare the SQL query to count new hires with today's date_hired
			String query = "SELECT COUNT(*) AS count FROM employees WHERE date_hired = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setDate(1, sqlDateToday);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("count"); // Return the count of new hires today
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace(); // Log or handle SQL error appropriately
		}

		return 0; // Default value if no results or error
	}

	public ResultSet getAllData() throws SQLException {
		if (connection == null) {
			throw new SQLException("Connection is null. Make sure to establish the connection.");
		}
		String query = "SELECT e.id, e.first_name, e.last_name, e.address, d.department_name AS department, "
				+ "t.work_type, e.rate, e.grossPay, e.netPay " + "FROM employees e "
				+ "LEFT JOIN types t ON e.work_type_id = t.id "
				+ "LEFT JOIN department d ON e.department_id = d.department_id";

		PreparedStatement statement = connection.prepareStatement(query);

		return statement.executeQuery();
	}

	/**
	 * Counts the total check-ins for today.
	 *
	 * @return the count of check-ins for today
	 */
	public int countCheckInsToday() {
		try {
			LocalDate today = LocalDate.now();
			Date sqlDateToday = Date.valueOf(today);

			String query = "SELECT COUNT(*) AS count FROM attendance WHERE date = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setDate(1, sqlDateToday);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("count"); // Return the count of check-ins today
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0; // Default if no result or error
	}

	public ResultSet getWorkTypesByDepartment(int departmentId) throws SQLException {
		String query = "SELECT work_type FROM types WHERE department_id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, departmentId);
		return ps.executeQuery();
	}

	public ResultSet getDepartments() throws SQLException {
		String query = "SELECT department_name FROM department";
		PreparedStatement ps = connection.prepareStatement(query);
		return ps.executeQuery();
	}

	public int getDepartmentId(String departmentName) {
		String query = "SELECT department_id FROM department WHERE department_name = ?";
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, departmentName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("department_id"); // Return the found department_id
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1; // Return -1 if not found
	}

	public int getWorkTypeId(String workTypeName) {
		String query = "SELECT id FROM types WHERE work_type = ?";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, workTypeName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("id"); // Return the found work_type_id
			} else {
				return -1; // Return -1 if not found
			}
		} catch (SQLException e) {
			System.err.println("Error fetching work type ID: " + e.getMessage());
			e.printStackTrace();
			return -1;
		}
	}

	public ResultSet getTypes() {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM types");
			return statement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public ResultSet getDeductions() {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM deductions");
			return statement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public void insertEMPData(String first_name, String last_name, String address, int workTypeId, double rate,
			String gender, String telNum, java.sql.Date DOB, String email, byte[] imageData, int departmentId,
			java.sql.Date date_hired, double grossPay, double netPay) {

		try {
			String sql = "INSERT INTO employees (id, first_name, last_name, address, work_type_id, rate, grossPay, netPay, gender, tel_number, DOB, email, profile_image, department_id, date_hired) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);

			int newId = getLastEmployeeId() + 1;
			statement.setInt(1, newId);
			statement.setString(2, first_name);
			statement.setString(3, last_name);
			statement.setString(4, address);
			statement.setInt(5, workTypeId);
			statement.setDouble(6, rate);
			statement.setDouble(7, grossPay);
			statement.setDouble(8, netPay);
			statement.setString(9, gender);
			statement.setString(10, telNum);

			// Get the Unix timestamp
			long unixDOB = DOB.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
			statement.setLong(11, unixDOB);
			statement.setString(12, email);
			statement.setBytes(13, imageData);
			statement.setInt(14, departmentId);

			long unixDate_hired = date_hired.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
					.getEpochSecond();
			statement.setLong(15, unixDate_hired);

			statement.executeUpdate();
			System.out.println("Record created.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertEMPCredentials(String name, String username, String passw, String role) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("INSERT INTO users (name, username, password, role) VALUES (?,?,?,?)");
			statement.setString(1, name);
			statement.setString(2, username);
			statement.setString(3, passw);
			statement.setString(4, role);

			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Helper method to get the last employee ID
	public int getLastEmployeeId() throws SQLException {
		int lastId = 0;
		PreparedStatement lastIdStatement = connection.prepareStatement("SELECT MAX(id) FROM employees");
		ResultSet rs = lastIdStatement.executeQuery();
		if (rs.next()) {
			lastId = rs.getInt(1); // Get the last ID
		}
		rs.close();
		return lastId;
	}

	public void deleteEmployeeData(int id, String name) {

		try {
			String sql = "DELETE FROM employees WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			statement.executeUpdate();
			System.out.println("Record deleted.");

			// Deleting from another table (example)
			try (PreparedStatement st = connection.prepareStatement("DELETE FROM users WHERE name = ?")) {
				st.setString(1, name);
				st.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateData(int id, String columnName, Object updatedValue) {
		try {
			String sql = "UPDATE employees SET " + columnName + " = ? WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setObject(1, updatedValue);
			statement.setInt(2, id);
			statement.executeUpdate();
			System.out.println("Record updated.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Employee getEmployeeById(int id) throws SQLException {
		String sql = "SELECT e.*, d.department_name, t.work_type " + "FROM employees e "
				+ "LEFT JOIN department d ON e.department_id = d.department_id "
				+ "LEFT JOIN types t ON e.work_type_id = t.id " + "WHERE e.id = ?";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setInt(1, id);
		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			Employee employee = new Employee();
			employee.setId(rs.getInt("id"));
			employee.setFirstName(rs.getString("first_name"));
			employee.setMiddleName(rs.getString("middle_name"));
			employee.setLastName(rs.getString("last_name"));
			employee.setAddress(rs.getString("address"));
			employee.setDepartment(rs.getString("department_name")); // Fetching department name from joined table
			employee.setWorkType(rs.getString("work_type")); // Fetching work type from joined table
			employee.setBasicSalary(rs.getDouble("rate"));
			employee.setGrossPay(rs.getDouble("grossPay"));
			employee.setNetPay(rs.getDouble("netPay")); // Assuming netPay is stored as a numeric type
			employee.setGender(rs.getString("gender"));
			employee.setTelNUmber(rs.getString("tel_number"));
			employee.setEmail(rs.getString("email"));
			// converts unix timestamp into a java.sql.Date
			employee.setDateHired(Date.valueOf(Instant.ofEpochSecond(rs.getLong("date_hired"))
					.atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalDate()));

			employee.setDOB(Date.valueOf(Instant.ofEpochSecond(rs.getLong("DOB")).atZone(ZoneId.systemDefault())
					.toLocalDateTime().toLocalDate()));

			byte[] imageData = rs.getBytes("profile_image");
			if (imageData != null) {
				employee.setProfileImage(imageData);
			}
			return employee;
		} else {
			return null; // No employee found with the given ID
		}
	}

	public synchronized void updateEmployee(Employee employee) {
		String sql = "UPDATE employees SET " + "first_name = ?," // 1
				+ " middle_name = ?, " // 2
				+ "last_name = ?, " // 3
				+ "address = ?, " // 4
				+ "department_id = ?, " // 5
				+ "work_type_id = ?, " // 6
				+ "rate = ?, " // 7
				+ "grossPay = ?, " // 8
				+ "netPay = ?, " // 9
				+ "gender = ?, " // 10
				+ "tel_number = ?, " // 11
				+ "email = ?, " // 12
				+ "profile_image = ?, " // 13
				+ "date_hired = ?, " // 14
				+ "DOB = ? " // 15
				+ "WHERE id = ?"; // 16

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, employee.getFirstName());
			statement.setString(2, employee.getMiddleName());
			statement.setString(3, employee.getLastName());
			statement.setString(4, employee.getAddress());
			statement.setInt(5, getDepartmentId(employee.getDepartment()));
			statement.setInt(6, getWorkTypeId(employee.getWorkType()));
			statement.setDouble(7, employee.getBasicSalary());
			statement.setDouble(8, employee.getGrossPay());
			statement.setDouble(9, employee.getNetPay());
			statement.setString(10, employee.getGender());
			statement.setString(11, employee.getTelNumber());
			statement.setString(12, employee.getEmail());
			if (employee.getProfileImage() != null) {
				statement.setBytes(13, employee.getProfileImageBytes());
			} else {
				statement.setNull(13, java.sql.Types.BLOB);
			}

			statement.setLong(14, employee.getDateHired().toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault())
					.toInstant().getEpochSecond());

			statement.setLong(15, employee.getDOB().toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault())
					.toInstant().getEpochSecond());

			statement.setInt(16, employee.getId());
			statement.executeUpdate();
			System.out.println("Record updated.");
		} catch (SQLException e) {
			System.err.println("Failed to update employee: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// attendance DB methods
	   /**
     * Checks whether an employee has checked in on a specific date.
     * 
     * @param employeeId The ID of the employee.
     * @param employeeName The name of the employee.
     * @param date The date to check in YYYY-MM-DD format.
     * @return true if the employee has checked in on the given date, false otherwise.
     */
    public boolean hasCheckedIn(int employeeId, String employeeName, String date) {
        // Query to check if an employee has checked in on a specific date
        String query = "SELECT COUNT(*) AS count FROM attendance WHERE employee_id = ? AND name = ? AND date = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            stmt.setString(2, employeeName);
            stmt.setString(3, date);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;  // If count is greater than 0, the employee has checked in
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking employee attendance: " + e.getMessage());
            // Optional: Rethrow the exception or handle it in a specific way
        }

        // Return false if there's an SQL exception or if the employee hasn't checked in
        return false;
    }


	public void addTimeIn(int employeeId, String name, String date, Long timeIn, String note) {
		try {
			String query = "INSERT INTO attendance (employee_id, name, date, time_in, time_out, clock_IN_Note) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, employeeId);
			stmt.setString(2, name);
			stmt.setString(3, date);
			stmt.setLong(4, timeIn);
			stmt.setLong(5, 0);
			stmt.setString(6, note);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); // Check if any SQL errors are raised
		}
	}

	public void updateTimeOut(int employeeId, String date, Long timeOut, String clockOUTnote, int overtime,
			double grossPay) {
		try {
			// SQL query to update time_out, note, and overtime
			String query = "UPDATE attendance SET time_out = ?, clock_OUT_Note = ?, overtime = ?, grossPay = ? WHERE employee_id = ? AND date = ?";
			PreparedStatement stmt = connection.prepareStatement(query);

			// Set parameters for the prepared statement
			stmt.setLong(1, timeOut); // Time out value
			stmt.setString(2, clockOUTnote); // Note text
			stmt.setInt(3, overtime); // Overtime hours
			stmt.setDouble(4, grossPay);
			stmt.setInt(5, employeeId); // Employee ID
			stmt.setString(6, date); // Attendance date

			// Execute the update
			stmt.executeUpdate();

			// Close the prepared statement
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace(); // Log any SQL errors
		}
	}

	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

	public void loadEMPAttendanceData(int employeeId, DefaultTableModel model) {
		try {
			String query = "SELECT * FROM attendance WHERE employee_id = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, employeeId);
			ResultSet rs = stmt.executeQuery();

			// Clear existing rows
			model.setRowCount(0);

			while (rs.next()) {
				// Get unix timestamp Time In and Time Out as java.sql.Time

				Long timeIn = rs.getLong("time_in");
				Long timeOut = rs.getLong("time_out");

				// Ensure using the correct time zone (Asia/Manila)
				ZoneId philippineZone = ZoneId.of("Asia/Manila");

				// Convert Unix timestamp to LocalDateTime with specified time zone
				String formattedTimeIn = (timeIn != null)
						? Instant.ofEpochSecond(timeIn).atZone(philippineZone).toLocalDateTime().format(timeFormatter)
						: "N/A";

				String formattedTimeOut;
				if (timeOut == 0) {
					formattedTimeOut = "N/A";
				} else {
					formattedTimeOut = Instant.ofEpochSecond(timeOut).atZone(philippineZone).toLocalDateTime()
							.format(timeFormatter);
				}

				// Add row to the table model with formatted time
				model.addRow(new Object[] {
						rs.getString("name"),
						rs.getString("date"),
						rs.getString("status"),
						formattedTimeIn,
						rs.getString("clock_IN_Note"),
						formattedTimeOut,
						rs.getString("clock_OUT_Note"),
						rs.getInt("overtime") + "Hours",
						rs.getString("grossPay")
				});

			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadAttendanceData(DefaultTableModel model) {
		try {
			String query = "SELECT * FROM attendance";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				Long timeIn = rs.getLong("time_in");
				Long timeOut = rs.getLong("time_out");

				// Convert to readable format
				String formattedTimeIn = (timeIn != null)
						? Instant.ofEpochSecond(timeIn).atZone(ZoneId.systemDefault()).toLocalDateTime()
								.format(timeFormatter)
						: "N/A";
				String formattedTimeOut = (timeOut != null)
						? Instant.ofEpochSecond(timeOut).atZone(ZoneId.systemDefault()).toLocalDateTime()
								.format(timeFormatter)
						: "N/A";

				model.addRow(new Object[] {
						rs.getString("date"),
						rs.getInt("employee_id"),
						rs.getString("name"),
						rs.getString("work_type"),
						rs.getString("status"),
						formattedTimeIn,
						rs.getString("clock_IN_Note"),
						formattedTimeOut,
						rs.getString("clock_OUT_Note"),
						rs.getInt("overtime"),
						rs.getString("grossPay") });
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes an attendance record for a specific employee on a given date.
	 * 
	 * @param employeeId The ID of the employee.
	 * @param date       The date of the attendance record to delete.
	 */
	public void deleteAttendance(int employeeId, String date) {
		String sql = "DELETE FROM attendance WHERE employee_id = ? AND date = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, employeeId);
			stmt.setString(2, date);
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				System.out.println("Attendance record deleted successfully.");
			} else {
				System.out.println("No attendance record found for deletion.");
			}
		} catch (SQLException e) {
			System.err.println("Error deleting attendance record: " + e.getMessage());
		}
	}

	// payroll DB methods
	public void insertPayroll(int employeeId, String name, String department, String workType, double grossPay,
			double ratePerDay, int daysWorked, int overtimeHours, double bonus, double totalDeduction, double netPay,
			java.sql.Date created_at) {

		String sql = "INSERT INTO payroll (employee_id, name, Department, workType, grossPay, ratePerDay, daysWorked, overtimeHours, bonus, totalDeduction, netPay, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			// Ensure the foreign key employeeId exists
			if (!doesEmployeeExist(employeeId)) {
				throw new SQLException("Employee with ID " + employeeId + " does not exist.");
			}

			statement.setInt(1, employeeId);
			statement.setString(2, name);
			statement.setString(3, department);
			statement.setString(4, workType);
			statement.setDouble(5, grossPay);
			statement.setDouble(6, ratePerDay);
			statement.setInt(7, daysWorked);
			statement.setInt(8, overtimeHours);
			statement.setDouble(9, bonus);
			statement.setDouble(10, totalDeduction);
			statement.setDouble(11, netPay);
			statement.setDate(12, created_at); // Date parameter
			statement.executeUpdate(); // Insert data into payroll

		} catch (SQLException e) {
			// Handle SQL exception appropriately
		}
	}

	public boolean doesEmployeeExist(int employeeId) {

		// Check if an employee with the given ID exists
		String sql = "SELECT COUNT(*) FROM employees WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, employeeId);
			ResultSet resultSet = statement.executeQuery();
			return resultSet.next() && resultSet.getInt(1) > 0; // Employee exists if count is greater than 0
		} catch (SQLException e) {

			return false;
		}
	}

	public void loadPayrollHistory(DefaultTableModel model) {

		String sql = "SELECT employee_id, name, Department, workType, grossPay, ratePerDay, daysWorked, "
				+ "overtimeHours, bonus, totalDeduction, netPay FROM payroll";

		try (PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

			// Clear existing rows in the table model
			model.setRowCount(0);

			while (rs.next()) {
				int employeeId = rs.getInt("employee_id");
				String name = rs.getString("name");
				String department = rs.getString("Department");
				String workType = rs.getString("workType");
				double grossPay = rs.getDouble("grossPay");
				double ratePerDay = rs.getDouble("ratePerDay");
				double daysWorked = rs.getDouble("daysWorked");
				double overtimeHours = rs.getDouble("overtimeHours");
				double bonus = rs.getDouble("bonus");
				double totalDeduction = rs.getDouble("totalDeduction");
				double netPay = rs.getDouble("netPay");

				model.addRow(new Object[] { employeeId, name, department, workType, grossPay, ratePerDay, daysWorked,
						overtimeHours, bonus, totalDeduction, netPay });
			}

		} catch (SQLException e) {
			System.err.println("Failed to load payroll history: " + e.getMessage());
		}
	}

	public void loadEMPPayroll(Employee currentEmployee, DefaultTableModel model, JTextArea textArea) {

		try {
			String sql = "SELECT * FROM payroll WHERE employee_id = ?"; // SQL query to fetch payroll data
			PreparedStatement statement = connection.prepareStatement(sql); // Prepare the SQL statement
			statement.setInt(1, currentEmployee.getId()); // Set the employee ID parameter

			ResultSet resultSet = statement.executeQuery(); // Execute the query

			// Iterate over the result set and add rows to the table model
			while (resultSet.next()) {
				// Retrieve data from the ResultSet
				String date = resultSet.getString("created_at"); // Assuming 'created_at' represents the date
				double basicSalary = resultSet.getDouble("ratePerDay");
				double grossPay = resultSet.getDouble("grossPay");
				double totalDeduction = resultSet.getDouble("totalDeduction");
				double netPay = resultSet.getDouble("netPay");
				String salaryPeriod = "Monthly"; // Example, could be derived from other data

				// Add data to the table model
				model.addRow(new Object[] { date, basicSalary, grossPay, totalDeduction, netPay, salaryPeriod });

				// Update text area with payroll details
				textArea.setText("Name: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + "\n"
						+ "Salary: " + basicSalary + "\n" + "Overtime: " + "0.00" + "\n" + // Assuming no overtime data
																							// available
						"Total Salary: " + grossPay + "\n\n" + "SSS: " + resultSet.getDouble("sss") + "\n"
						+ "Pag-IBig: " + resultSet.getDouble("pagibig") + "\n" + "Philhealth: "
						+ resultSet.getDouble("philhealth") + "\n" + "Total Deductions: " + totalDeduction + "\n\n"
						+ "Net Pay: " + netPay);
			}

		} catch (SQLException e) {
			e.printStackTrace(); // Handle SQL exceptions
			JOptionPane.showMessageDialog(null, "Error loading payroll data: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}