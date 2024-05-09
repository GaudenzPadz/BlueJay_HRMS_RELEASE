package bluejay.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import bluejay.Employee;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class PayrollPanel extends JPanel implements Printable {
	private static final long serialVersionUID = 1L;
	private JTextField tfEmployeeID, tfEmployeeName, tfEmployeeDepartment, tfEmployeeWorkType, tfRatePerDay,
			tfDaysWorked, tfOvertimeHours, tfGrossPay, tfOvertimeRate, tfDeductionsSSS, tfDeductionsPagIbig,
			tfDeductionsPhilHealth, tfTotalDeductions, tfAdvanced, tfBonus, tfNetPay;
	private JTable payrollHistoryTable;
	private EmployeeDatabase db;
	private List<Employee> employees;
	private DefaultTableModel EMPListModel;
	private JTable EMPListTable;
	private DefaultTableModel tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Department", "Work Type",
			"Gross Pay", "Rate Pe rDay", "Days Worked", "Overtime Hours", "Bonus", "Total Deductions", "Net Pay" }, 0);
	private JTextField employmentTypeField;
	private JTextField wageField;
	private JTextField ProjectCompletedField;

	public PayrollPanel(EmployeeDatabase db) {
		this.db = db;
		this.employees = db.getAllEmployees(); // Fetch all employees

		setLayout(new BorderLayout());
		JScrollPane mainScrollPane = new JScrollPane();
		add(mainScrollPane, BorderLayout.CENTER);

		JPanel mainPanel = new JPanel(
				new MigLayout("wrap, fillx, insets 25 35 25 35", "[center]", "[center][grow][][]"));
		mainScrollPane.setViewportView(mainPanel);

		// Setup employee selection panel
		JPanel selectEMP = setupEmployeeSelectorPanel();
		mainPanel.add(selectEMP, "growx");

		// Setup payroll calculation panel
		JPanel payrollCalculationPanel = setupPayrollCalculationPanel();
		mainPanel.add(payrollCalculationPanel, "growx");
	}

	private JPanel setupEmployeeSelectorPanel() {
		JPanel selectEmployeePanel = new JPanel(
				new MigLayout("", "[100px,grow,center][][][][grow][][][][][100px,grow,center]", "[center][][80px]"));

		EMPListModel = new DefaultTableModel(
				new String[] { "Employee ID", "First Name", "Last Name", "Department", "Work Type", "Select" }, 0);

		EMPListTable = new JTable(EMPListModel);
		EMPListTable.getColumn("Select").setCellRenderer(new ButtonCellRenderer()); // Custom renderer for "Select"
																					// button
		EMPListTable.addMouseListener(new EmployeeSelectionListener()); // Add listener for row selection

		JScrollPane empListScrollPane = new JScrollPane(EMPListTable);
		selectEmployeePanel.add(empListScrollPane, "cell 0 2 10 1,growx");

		JLabel lblSelectEmployee = new JLabel("Select Employee");
		lblSelectEmployee.setFont(new Font("SansSerif", Font.BOLD, 20));
		selectEmployeePanel.add(lblSelectEmployee, "cell 0 0 10 1,alignx center");

		JTextField searchField = new JTextField();
		selectEmployeePanel.add(new JLabel("Search"), "cell 1 1");
		selectEmployeePanel.add(searchField, "cell 2 1 4 1,growx");
		searchField.getDocument().addDocumentListener(new EmployeeSearchListener(searchField));

		populateEmployeeList(); // Populate the list of employees

		return selectEmployeePanel;
	}

	private void populateEmployeeList() {
		EMPListModel.setRowCount(0); // Clear existing rows

		for (Employee employee : employees) {
			EMPListModel.addRow(new Object[] { employee.getId(), employee.getFirstName(), employee.getLastName(),
					employee.getDepartment(), employee.getWorkType(), "Select" });
		}
	}

	private JPanel setupPayrollCalculationPanel() {
		JPanel payrollCalculationPanel = new JPanel(new MigLayout("", "[grow,center][100px][100px,grow][50px][100px][100px,grow][50px][100px][100px,grow][50px][grow,center]", "[center][][][][][][][][][][][][][][100px,grow][center]"));

		JLabel lblPayrollCalculator = new JLabel("Payroll Calculator");
		lblPayrollCalculator.setFont(new Font("SansSerif", Font.BOLD, 20));
		payrollCalculationPanel.add(lblPayrollCalculator, "cell 1 1 2 1");

		JLabel lblSalary = new JLabel("Salary");
		lblSalary.setFont(new Font("SansSerif", Font.BOLD, 20));
		payrollCalculationPanel.add(lblSalary, "cell 4 2 2 1");

		// Define labels and fields for payroll calculation
		JLabel lblEmployeeID = new JLabel("Employee ID:");
		payrollCalculationPanel.add(lblEmployeeID, "cell 1 3,alignx left");
		tfEmployeeID = new JTextField(4);
		payrollCalculationPanel.add(tfEmployeeID, "cell 2 3,growx");

		JLabel lblGrossPay = new JLabel("Gross Pay:");
		payrollCalculationPanel.add(lblGrossPay, "cell 4 3,alignx left");
		tfGrossPay = new JTextField();
		payrollCalculationPanel.add(tfGrossPay, "cell 5 3,growx");

		JLabel lblEmployeeName = new JLabel("Employee Name:");
		payrollCalculationPanel.add(lblEmployeeName, "cell 1 4,alignx left");
		tfEmployeeName = new JTextField();
		tfEmployeeName.setEditable(false);
		payrollCalculationPanel.add(tfEmployeeName, "cell 2 4,growx");

		JLabel lblRatePerDay = new JLabel("Rate Per Day:");
		payrollCalculationPanel.add(lblRatePerDay, "cell 4 4,alignx left");
		tfRatePerDay = new JTextField();
		payrollCalculationPanel.add(tfRatePerDay, "cell 5 4,growx");

		JLabel lblEmployeeDepartment = new JLabel("Department:");
		payrollCalculationPanel.add(lblEmployeeDepartment, "cell 1 5,alignx left");
		tfEmployeeDepartment = new JTextField();
		tfEmployeeDepartment.setEditable(false);
		payrollCalculationPanel.add(tfEmployeeDepartment, "cell 2 5,growx");

		JLabel lblNetPay = new JLabel("Net Pay:");

		JLabel lblDaysWorked = new JLabel("Days Worked:");
		payrollCalculationPanel.add(lblDaysWorked, "cell 4 5,alignx left");
		tfDaysWorked = new JTextField();
		payrollCalculationPanel.add(tfDaysWorked, "cell 5 5,growx");
		
		JLabel lblNewLabel = new JLabel("Employment Type:");
		payrollCalculationPanel.add(lblNewLabel, "cell 1 6");
		
		employmentTypeField = new JTextField();
		payrollCalculationPanel.add(employmentTypeField, "cell 2 6,growx");
		employmentTypeField.setColumns(10);
		employmentTypeField.setText("Part Time");
		
		JLabel lb = new JLabel("Rate:");
		payrollCalculationPanel.add(lb, "cell 4 6,alignx left");
		
		wageField = new JTextField();
		payrollCalculationPanel.add(wageField, "cell 5 6,growx");
		wageField.setColumns(10);

		JLabel lbWorkType = new JLabel("Work Type");
		payrollCalculationPanel.add(lbWorkType, "cell 1 7,alignx left");

		tfEmployeeWorkType = new JTextField();
		tfEmployeeWorkType.setEditable(false);
		payrollCalculationPanel.add(tfEmployeeWorkType, "cell 2 7,growx");
		tfEmployeeWorkType.setColumns(10);

		JLabel lblOvertimeHours = new JLabel("Overtime Hours:");
		payrollCalculationPanel.add(lblOvertimeHours, "cell 4 7,alignx left");
		tfOvertimeHours = new JTextField();
		payrollCalculationPanel.add(tfOvertimeHours, "cell 5 7,growx");

		JLabel lbOvertimeRate = new JLabel("Overtime Rate");
		payrollCalculationPanel.add(lbOvertimeRate, "cell 4 8,alignx left");

		// base the OT rate by the hours of overtime
		// if 2 hours overtime then OTrate = 400
		// 1 hour is == 200 otRate
		tfOvertimeRate = new JTextField(10);
		tfOvertimeRate.setEditable(false);
		payrollCalculationPanel.add(tfOvertimeRate, "cell 5 8,growx");

		JLabel lbBonus = new JLabel("Bonus");
		payrollCalculationPanel.add(lbBonus, "cell 4 9,alignx left");

		tfBonus = new JTextField(10);
		payrollCalculationPanel.add(tfBonus, "cell 5 9,growx");
		payrollCalculationPanel.add(lblNetPay, "cell 7 9,alignx center");

		// Deductions
		JLabel lblDeductions = new JLabel("Deductions");
		lblDeductions.setFont(new Font("SansSerif", Font.BOLD, 20));
		payrollCalculationPanel.add(lblDeductions, "cell 7 2,alignx center");

		JLabel lblSSS = new JLabel("SSS:");
		tfDeductionsSSS = new JTextField();
		payrollCalculationPanel.add(lblSSS, "cell 7 3,alignx left");
		payrollCalculationPanel.add(tfDeductionsSSS, "cell 8 3,growx");

		JLabel lblPagIbig = new JLabel("PAG-Ibig:");
		tfDeductionsPagIbig = new JTextField();
		payrollCalculationPanel.add(lblPagIbig, "cell 7 4,alignx left");
		payrollCalculationPanel.add(tfDeductionsPagIbig, "cell 8 4,growx");

		JLabel lblPhilHealth = new JLabel("PhilHealth:");
		tfDeductionsPhilHealth = new JTextField();
		payrollCalculationPanel.add(lblPhilHealth, "cell 7 5,alignx left");
		payrollCalculationPanel.add(tfDeductionsPhilHealth, "cell 8 5,growx");

		JLabel lblAdvanced = new JLabel("Advanced:");
		tfAdvanced = new JTextField();
		payrollCalculationPanel.add(lblAdvanced, "cell 7 7,alignx left");
		payrollCalculationPanel.add(tfAdvanced, "cell 8 7,growx");

		JLabel lblTotalDeductions = new JLabel("Total Deductions:");
		tfTotalDeductions = new JTextField();
		payrollCalculationPanel.add(lblTotalDeductions, "cell 7 8,alignx left");
		payrollCalculationPanel.add(tfTotalDeductions, "cell 8 8,growx");

		JButton btnCalculate = new JButton("Calculate");
		btnCalculate.addActionListener(e -> calculatePayroll());

		tfNetPay = new JTextField(10);
		tfNetPay.setEditable(false);
		payrollCalculationPanel.add(tfNetPay, "cell 8 9,growx");
		
		JLabel ProjectCompleted = new JLabel("Project Completed: ");
		payrollCalculationPanel.add(ProjectCompleted, "cell 4 10,alignx left");
		
		ProjectCompletedField = new JTextField();
		payrollCalculationPanel.add(ProjectCompletedField, "cell 5 10,growx");
		ProjectCompletedField.setColumns(10);
		payrollCalculationPanel.add(btnCalculate, "cell 7 11,growx");

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(e -> clearFields());
		payrollCalculationPanel.add(btnClear, "cell 8 11,growx");

		JButton btnRefresh = new JButton("Refresh");
		// a button to refresh the fields
		// also the history table
		payrollCalculationPanel.add(btnRefresh, "flowx,cell 7 12,growx");

		JButton btnPrint = new JButton("Print Payroll");
		btnPrint.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Document document = new Document();
				try {
					PdfWriter.getInstance(document, new FileOutputStream("PayrollDetails.pdf"));
					document.open();
					com.itextpdf.text.Font font = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
					
					document.add(new Paragraph("Payroll Details", font));
					document.add(new Paragraph("Employee Name: " + tfEmployeeName.getText(), font));
					document.add(new Paragraph("Employee ID: " + tfEmployeeID.getText(), font));
					document.add(new Paragraph("Department: " + tfEmployeeDepartment.getText(), font));
					document.add(new Paragraph("Work Type: " + tfEmployeeWorkType.getText(), font));
					document.add(new Paragraph("Days Worked: " + tfDaysWorked.getText(), font));
					document.add(new Paragraph("Rate Per Day: " + tfRatePerDay.getText(), font));
					document.add(new Paragraph("Gross Pay: " + tfGrossPay.getText(), font));
					document.add(new Paragraph("Net Pay: " + tfNetPay.getText(), font));
					document.add(new Paragraph("Deductions: ", font));
					document.add(new Paragraph("   SSS: " + tfDeductionsSSS.getText(), font));
					document.add(new Paragraph("   PhilHealth: " + tfDeductionsPhilHealth.getText(), font));
					document.add(new Paragraph("   PAG-IBIG: " + tfDeductionsPagIbig.getText(), font));
					document.add(new Paragraph("Date: " + new Date(System.currentTimeMillis()).toString(), font));
					
					document.close();
					JOptionPane.showMessageDialog(null, "PDF file was created successfully!");
				} catch (DocumentException | FileNotFoundException ex) {
					JOptionPane.showMessageDialog(null, "Error in PDF creation: " + ex.getMessage());
				}
			}
		});
		payrollCalculationPanel.add(btnPrint, "cell 8 12,grow");

		JLabel lblPayrollHistory = new JLabel("Payroll History");
		lblPayrollHistory.setFont(new Font("SansSerif", Font.BOLD, 20));
		payrollCalculationPanel.add(lblPayrollHistory, "cell 1 13 5 1");

		// Table for payroll history
		payrollHistoryTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(payrollHistoryTable);
		payrollCalculationPanel.add(scrollPane, "cell 0 14 11 1,grow");

		refreshTable(tableModel); // Refresh the payroll history on initialization

		return payrollCalculationPanel;
	}

	private void calculatePayroll() {
		try {
			// Validate crucial fields
			if (tfEmployeeID.getText().isEmpty()) {
				throw new IllegalArgumentException("Employee ID is required.");
			}

			// Get the employee ID from the form
			int employeeId = Integer.parseInt(tfEmployeeID.getText());

			// Ensure the employee exists in the database
			if (!db.doesEmployeeExist(employeeId)) {
				throw new SQLException("Employee with ID " + employeeId + " does not exist.");
			}

			// Additional form fields
			String employeeName = tfEmployeeName.getText();
			String employeeDepartment = tfEmployeeDepartment.getText();
			String employeeWorkType = tfEmployeeWorkType.getText();
			double ratePerDay = Double.parseDouble(tfRatePerDay.getText());
			int daysWorked = Integer.parseInt(tfDaysWorked.getText());
			double overtimeHours = Double.parseDouble(tfOvertimeHours.getText());
			double advanced = Double.parseDouble(tfAdvanced.getText());
			double bonus = Double.parseDouble(tfBonus.getText());

			// Calculate payroll
			if (employmentTypeField.getText().equalsIgnoreCase("part time")) {
				double partTimeRate = ratePerDay * daysWorked * 0.5;
				System.out.println(partTimeRate);
				tfNetPay.setText(String.format("%.2f", partTimeRate));
				//disable overtime, bonus, overtimerate, and deductions  wageField 
				//pag part time yung ung rate per day ay mahahati
            } else if (employmentTypeField.getText().equalsIgnoreCase("Project Based")) 											{
				double projectBasedRate = ratePerDay * Integer.parseInt(ProjectCompletedField.getText());
				System.out.println(projectBasedRate);
				tfNetPay.setText(String.format("%.2f", projectBasedRate));
				wageField.setText(String.valueOf(ratePerDay));
				//disable all salary and deduction except grosspay, rate, and project completed
				//pag project based yung ung rate per day ay i mumultiply sa project completed
			} else if  (employmentTypeField.getText().equalsIgnoreCase("full time")) {
		       //disable project completed
			
				double basicSalary = ratePerDay * daysWorked;
				double overTimePay = overtimeHours * ratePerDay; // Use the same rate per day
				double grossPay = basicSalary + overTimePay + bonus;
				// Deductions
				double sss = Double.parseDouble(tfDeductionsSSS.getText());
				double pagIbig = Double.parseDouble(tfDeductionsPagIbig.getText());
				double philHealth = Double.parseDouble(tfDeductionsPhilHealth.getText());
				double totalDeductions = sss + pagIbig + philHealth + advanced;
				double netPay = grossPay - totalDeductions;

				// Update the form with calculated values
				tfGrossPay.setText(String.format("%.2f", grossPay));
				tfTotalDeductions.setText(String.format("%.2f", totalDeductions));
				tfNetPay.setText(String.format("%.2f", netPay));

				// Save the payroll record to the database
				db.insertPayroll(employeeId, employeeName, employeeDepartment, employeeWorkType, grossPay, ratePerDay,
						daysWorked, (int) overtimeHours, bonus, totalDeductions, netPay,
						new Date(System.currentTimeMillis())); // Use the current date

				// Refresh the table to show the new record
				refreshTable(tableModel);
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Please enter valid numeric values for all payroll fields.",
					"Input Error", JOptionPane.ERROR_MESSAGE);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "SQL Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void refreshTable(DefaultTableModel model) {
		model.setRowCount(0); // Clear existing rows
		db.loadPayrollHistory(model); // Load the updated payroll history
	}

	private void clearFields() {
		tfEmployeeID.setText("");
		tfEmployeeName.setText("");
		tfEmployeeDepartment.setText("");
		tfEmployeeWorkType.setText("");
		tfRatePerDay.setText("");
		tfDaysWorked.setText("");
		tfOvertimeHours.setText("");
		tfGrossPay.setText("");
		tfNetPay.setText("");
		tfDeductionsSSS.setText("");
		tfDeductionsPagIbig.setText("");
		tfDeductionsPhilHealth.setText("");
		tfTotalDeductions.setText("");
		tfAdvanced.setText("");
		tfBonus.setText("");
	}

	private class ButtonCellRenderer implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return new JButton("Select");
		}
	}

	private class EmployeeSelectionListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			int row = EMPListTable.rowAtPoint(e.getPoint());
			int col = EMPListTable.columnAtPoint(e.getPoint());

			if (col == 5) { // "Select" button column
				selectEmployee(row);
			}
		}

		private void selectEmployee(int row) {
			Employee selectedEmployee = employees.get(row);

			tfEmployeeID.setText(String.valueOf(selectedEmployee.getId()));
			tfEmployeeName.setText(selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName());
			tfEmployeeDepartment.setText(selectedEmployee.getDepartment());
			tfEmployeeWorkType.setText(selectedEmployee.getWorkType());
			tfRatePerDay.setText(String.valueOf(selectedEmployee.getRatePerDay()));

			loadDeductions();

			revalidate();
			repaint();
		}
	}

	private class EmployeeSearchListener implements DocumentListener {
		private final JTextField searchField; // Reference to the search field

		public EmployeeSearchListener(JTextField searchField) {
			this.searchField = searchField; // Assign the search field reference
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			filterEmployeeList();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			filterEmployeeList();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// Typically, this isn't used in plain text fields
		}

		private void filterEmployeeList() {
			// Ensure searchField isn't null
			if (searchField == null) {
				return;
			}

			String searchText = searchField.getText().toLowerCase(); // Get the text from the search field
			EMPListModel.setRowCount(0); // Clear existing rows

			// Loop through employees to find matches based on first or last name
			for (Employee employee : employees) {
				if (employee.getFirstName().toLowerCase().contains(searchText)
						|| employee.getLastName().toLowerCase().contains(searchText)) {
					EMPListModel.addRow(new Object[] { employee.getId(), employee.getFirstName(),
							employee.getLastName(), employee.getDepartment(), employee.getWorkType(), "Select" // The
																												// "Select"
																												// button
					});
				}
			}
		}
	}

	private void loadDeductions() {
		ResultSet rs = db.getDeductions();
		try {
			if (rs.next()) {
				tfDeductionsSSS.setText(String.valueOf(rs.getInt("SSS")));
				tfDeductionsPagIbig.setText(String.valueOf(rs.getInt("PAG_IBIG")));
				tfDeductionsPhilHealth.setText(String.valueOf(rs.getInt("PHILHEALTH")));
				tfAdvanced.setText(String.valueOf(rs.getInt("advanced")));

				rs.close();

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
	    if (pageIndex > 0) {
	        return NO_SUCH_PAGE;
	    }

	    Graphics2D g2d = (Graphics2D) g;
	    g2d.translate(pf.getImageableX(), pf.getImageableY());

	    // Customize printing font and layout
	    Font font = new Font("Arial", Font.PLAIN, 12);
	    g2d.setFont(font);
	    int x = 50; // x-coordinate for printing
	    int y = 30; // Adjusted y-coordinate for starting the box below the header
	    int payslipHeight = 350; // Height of the payslip rectangle

	    // Draw payslip box
	    g2d.drawRect(x, y, 300, payslipHeight);

	    // Display net pay
	    String formattedNetPay = String.format("%.2f", Double.parseDouble(tfNetPay.getText()));

	    // Print header
	    g2d.drawString("Weld Well Payslip", x + 100, y + 20);
	    g2d.drawString("Net Pay: " + formattedNetPay, x + 10, y + 230);
	    // Print payslip details
	    g2d.drawString("--------------------------------------", x + 10, y + 40);
	    g2d.drawString("Employee Information", x + 10, y + 50);
	    g2d.drawString("--------------------------------------", x + 10, y + 60);
	    g2d.drawString("Employee Name: " + tfEmployeeName.getText(), x + 10, y + 70);
	    g2d.drawString("Employee Number: " + tfEmployeeID.getText(), x + 10, y + 85);
	    g2d.drawString("Department: " + tfEmployeeDepartment.getText(), x + 10, y + 100);
	    g2d.drawString("Work Type: " + tfEmployeeWorkType.getText(), x + 10, y + 115);
	    g2d.drawString("Days Worked: " + tfDaysWorked.getText(), x + 10, y + 130);
	    g2d.drawString("Daily Rate: " + tfRatePerDay.getText(), x + 10, y + 145);
	    g2d.drawString("--------------------------------------", x + 10, y + 155);
	    g2d.drawString("Earnings:", x + 10, y + 165);
	    g2d.drawString("SSS: " + tfDeductionsSSS.getText(), x + 10, y + 180);
	    g2d.drawString("PhilHealth Premium: " + tfDeductionsPhilHealth.getText(), x + 10, y + 195);
	    g2d.drawString("Pag-IBIG Premium: " + tfDeductionsPagIbig.getText(), x + 10, y + 210);
	    g2d.drawString("--------------------------------------", x + 10, y + 220);

	    // Print the date below the payslip details
	    String dateString = "Date: " + new Date(System.currentTimeMillis()).toString();
	    g2d.drawString("--------------------------------------", x + 10, y + 265);
	    g2d.drawString(dateString, x + 10, y + 280);
	    g2d.drawString("--------------------------------------", x + 10, y + 295);
	    g2d.drawString("Comapany Gmail:WeldWellOfcial@gmail.com", x + 10, y + 310);
	    g2d.drawString("Contact Number:09084130846", x + 10, y + 325);
	    g2d.drawString("--------------------------------------", x + 10, y + 345);

	    return PAGE_EXISTS;
	}
}
