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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import bluejay.Employee;
import bluejay.Payroll;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class PayrollPanel extends JPanel implements Printable {
	private static final long serialVersionUID = 1L;
	private JTextField tfEmployeeID, tfEmployeeName, tfEmployeeDepartment, tfEmployeeWorkType, tfEmpType, tfRatePerHour,
			tfDaysWorked, tfOvertimeHours, tfGrossPay, tfDeductionsSSS, tfDeductionsPagIbig,
			tfDeductionsPhilHealth, tfTotalDeductions, tfAdvanced, tfBonus, tfNetPay, PBRatePerProjectField,
			PBNumOfProjectsField;
	private JTable payrollHistoryTable;
	private EmployeeDatabase db;
	private List<Employee> employees;
	private JTable EMPListTable;
	private DefaultTableModel EMPListModel = new DefaultTableModel(
			new String[] { "ID", "Name", "Department", "Employment Type", "Work Type", "Select" }, 0);
	private DefaultTableModel payrollHistoryTableModel = new DefaultTableModel(
			new String[] { "ID", "Name", "Department", "Work Type", "Gross Pay", "Rate Pe rDay", "Days Worked",
					"Overtime Hours", "Bonus", "Total Deductions", "Net Pay" },
			0);
	private JLabel GrossPayLabel;
	private JLabel RatePerHourLabel;
	private JLabel DaysWorkedLabel;
	private JLabel OvertimeHoursLabel;
	private JLabel BonusLabel;
	private JLabel NetPayLabel;
	private Employee selectedEmployee; // Class member to hold the currently selected employee
	private JPanel salaryPanel;

	public PayrollPanel(EmployeeDatabase db) {
		this.db = db;
		this.employees = db.getAllEmployees(); // Fetch all employees

		setLayout(new BorderLayout());
		JScrollPane mainScrollPane = new JScrollPane();
		add(mainScrollPane, BorderLayout.CENTER);

		intializeComponents();

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

	private void intializeComponents() {
		tfEmployeeID = new JTextField(10);
		tfEmployeeName = new JTextField(10);
		tfEmployeeDepartment = new JTextField(20);
		tfEmployeeWorkType = new JTextField(20);
		tfRatePerHour = new JTextField(20);
		tfDaysWorked = new JTextField("0"); // Set default text as "0"
		tfGrossPay = new JTextField();
		tfDeductionsSSS = new JTextField();
		tfDeductionsPagIbig = new JTextField();
		tfDeductionsPhilHealth = new JTextField();
		tfAdvanced = new JTextField();
		payrollHistoryTable = new JTable(payrollHistoryTableModel);
		tfOvertimeHours = new JTextField();
		tfBonus = new JTextField();
		tfNetPay = new JTextField();
		tfDaysWorked = new JTextField();
		tfNetPay = new JTextField();

		PBRatePerProjectField = new JTextField();
		PBNumOfProjectsField = new JTextField(10);

		tfEmpType = new JTextField();

	}

	private JPanel setupEmployeeSelectorPanel() {
		JPanel selectEmployeePanel = new JPanel(
				new MigLayout("", "[100px,grow,center][][][][grow][][][][][100px,grow,center]", "[center][][80px]"));
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
			EMPListModel.addRow(new Object[] { employee.getEmployeeId(), employee.getFirstName(),
					employee.getLastName(), employee.getDepartment(), employee.getWorkType(), "Select" });
		}
	}

	private JPanel setupPayrollCalculationPanel() {
		JPanel payrollCalculationPanel = new JPanel(new MigLayout("", "[160px,grow][50px][90px,grow][50px][90px,grow]",
				"[center][][][][][][][grow][grow][][grow][][grow][][100px,grow][center]"));

		// btnCalculate.addActionListener(e -> calculatePayroll());

		JLabel lblPayrollCalculator = new JLabel("Payroll Calculator");
		lblPayrollCalculator.setFont(new Font("SansSerif", Font.BOLD, 20));
		payrollCalculationPanel.add(lblPayrollCalculator, "cell 0 0");

		// employee information pane
		JPanel empInfoPanel = EmployeeInformationPane();

		payrollCalculationPanel.add(empInfoPanel, "cell 0 2 1 5,grow");

		// salaryPane
		salaryPanel = salaryPane();
		payrollCalculationPanel.add(salaryPanel, "cell 2 2 1 9,grow");

		// deductions pane
		JPanel deductionPanel = deductionPane();
		payrollCalculationPanel.add(deductionPanel, "cell 4 2 1 9,grow");

		// // project based panel
		// JPanel projectBasedPanel = projectBasedPane();
		// payrollCalculationPanel.add(projectBasedPanel, "cell 4 11 2 2,grow");

		JButton btnRefresh = new JButton("Refresh");
		payrollCalculationPanel.add(btnRefresh, "flowx,cell 4 11,growx,aligny center");

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(e -> clearFields());
		payrollCalculationPanel.add(btnClear, "flowx,cell 4 12,growx");

		JLabel lblPayrollHistory = new JLabel("Payroll History");
		lblPayrollHistory.setFont(new Font("SansSerif", Font.BOLD, 20));
		payrollCalculationPanel.add(lblPayrollHistory, "cell 0 13 3 1");

		// Table for payroll history
		payrollHistoryTable = new JTable(payrollHistoryTableModel);
		JScrollPane scrollPane = new JScrollPane(payrollHistoryTable);
		payrollCalculationPanel.add(scrollPane, "cell 0 14 5 1,grow");

		JButton btnPrint = new JButton("Print Payroll");
		btnPrint.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Document document = new Document();
				try {
					PdfWriter.getInstance(document, new FileOutputStream("PayrollDetails.pdf"));
					document.open();
					com.itextpdf.text.Font font = new com.itextpdf.text.Font(
							com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);

					document.add(new Paragraph("Payroll Details", font));
					document.add(new Paragraph("Employee Name: " + tfEmployeeName.getText(), font));
					document.add(new Paragraph("Employee ID: " + tfEmployeeID.getText(), font));
					document.add(new Paragraph("Department: " + tfEmployeeDepartment.getText(), font));
					document.add(new Paragraph("Work Type: " + tfEmployeeWorkType.getText(), font));
					document.add(new Paragraph("Days Worked: " + tfDaysWorked.getText(), font));
					document.add(new Paragraph("Rate Per Hour: " + tfRatePerHour.getText(), font));
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
		payrollCalculationPanel.add(btnPrint, "cell 4 12,growx,aligny center");

		refreshTable(payrollHistoryTableModel); // Refresh the payroll history on initialization

		return payrollCalculationPanel;
	}

	private JPanel EmployeeInformationPane() {
		JPanel empInfoPanel = new JPanel(new MigLayout("fill,insets 10", "[95px][86px]", "[][][][][]"));
		empInfoPanel.setBorder(
				new TitledBorder(null, "Employee Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		// Define labels and fields for payroll calculation
		empInfoPanel.add(new JLabel("Employee ID:"), "cell 0 0");
		empInfoPanel.add(tfEmployeeID, "cell 1 0,growx");
		tfEmployeeID.setEditable(false);

		empInfoPanel.add(new JLabel("Employee Name:"), "cell 0 1");
		empInfoPanel.add(tfEmployeeName, "cell 1 1,growx");
		tfEmployeeName.setEditable(false);

		empInfoPanel.add(new JLabel("Department:"), "cell 0 2");
		empInfoPanel.add(tfEmployeeDepartment, "cell 1 2,growx");
		tfEmployeeDepartment.setEditable(false);

		empInfoPanel.add(new JLabel("Employment Type:"), "cell 0 3");
		empInfoPanel.add(tfEmpType, "cell 1 3,growx");
		tfEmpType.setEditable(false);

		empInfoPanel.add(new JLabel("Work Type"), "cell 0 4");
		empInfoPanel.add(tfEmployeeWorkType, "cell 1 4,growx");
		tfEmployeeWorkType.setEditable(false);
		return empInfoPanel;
	}

	private JPanel salaryPane() {
		JPanel salaryPane = new JPanel(new BorderLayout());

		// based on the selected employee at employee list table
		if (selectedEmployee == null) {
			salaryPane.add(new JLabel("Select an employee from the list."), BorderLayout.CENTER);
			return salaryPane;
		}
		salaryPane.add(fullTimePane(), BorderLayout.CENTER);

		String employmentType = selectedEmployee.getEmploymentType();

		switch (employmentType) {
			case "Part Time":
				salaryPane.removeAll();
				salaryPane.add(partTimePane(), BorderLayout.CENTER);
				break;
			case "Full Time":
				salaryPane.removeAll();
				salaryPane.add(fullTimePane(), BorderLayout.CENTER);
				break;
			case "Project Based":
				salaryPane.removeAll();
				salaryPane.add(projectBasedPane(), BorderLayout.CENTER);
				break;
			default:
				salaryPane.removeAll();
				salaryPane.add(new JLabel("Select Employee Above"), BorderLayout.CENTER);
				break;
		}
		return salaryPane;
	}

	private JPanel fullTimePane() {
		JPanel fullTimePanel = new JPanel(
				new MigLayout("fill,insets 10", "[95px][86px]", "[][][][][][][][][][][][][][][]"));
		fullTimePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JLabel lblSalary = new JLabel("Full Time Salary");
		fullTimePanel.add(lblSalary, "cell 0 0 2 1");
		lblSalary.setFont(new Font("SansSerif", Font.BOLD, 20));

		GrossPayLabel = new JLabel("Gross Pay:");
		fullTimePanel.add(GrossPayLabel, "cell 0 1");

		fullTimePanel.add(tfGrossPay, "cell 1 1,growx");

		RatePerHourLabel = new JLabel("Rate Per Hour:");
		fullTimePanel.add(RatePerHourLabel, "cell 0 2");

		fullTimePanel.add(tfRatePerHour, "cell 1 2,growx");

		DaysWorkedLabel = new JLabel("Days Worked:");
		fullTimePanel.add(DaysWorkedLabel, "cell 0 3");
		fullTimePanel.add(tfDaysWorked, "cell 1 3,growx");

		OvertimeHoursLabel = new JLabel("Overtime Hours:");
		fullTimePanel.add(OvertimeHoursLabel, "cell 0 4");

		fullTimePanel.add(tfOvertimeHours, "cell 1 4,growx");

		BonusLabel = new JLabel("Bonus");
		fullTimePanel.add(BonusLabel, "cell 0 5");

		fullTimePanel.add(tfBonus, "cell 1 5,growx");

		NetPayLabel = new JLabel("Net Pay:");
		fullTimePanel.add(NetPayLabel, "cell 0 6,alignx left");

		JButton fullTimeBtnCalculate = new JButton("Calculate Full Time");
		fullTimeBtnCalculate.addActionListener(e -> fullTimeMethodCalculation());

		fullTimePanel.add(tfNetPay, "cell 1 6,growx");
		fullTimePanel.add(fullTimeBtnCalculate, "cell 0 13 2 1,growx");

		return fullTimePanel;
	}

	private JPanel projectBasedPane() {
		JPanel projectBasedPanel = new JPanel(
				new MigLayout("fill,insets 10", "[95px,grow][86px,grow]", "[][][][][][][][][][][][][20px][]"));
		projectBasedPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JLabel PBSalaryLabel = new JLabel("Project Based Salary");
		PBSalaryLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		projectBasedPanel.add(PBSalaryLabel, "cell 0 0 2 1");

		projectBasedPanel.add(new JLabel("Gross Pay"), "cell 0 1,alignx left");

		projectBasedPanel.add(tfGrossPay, "cell 1 1,growx");

		projectBasedPanel.add(new JLabel("Rate per Project"), "cell 0 2");

		projectBasedPanel.add(PBRatePerProjectField, "cell 1 2,growx");

		projectBasedPanel.add(new JLabel("Number of Projects Completed"), "cell 0 3,alignx left");

		projectBasedPanel.add(PBNumOfProjectsField, "cell 1 3,growx");

		JButton PBCalculateBtn = new JButton("Calculate Project Based");
		PBCalculateBtn.addActionListener(e -> projectBasedMethodCalculation());

		JLabel label = new JLabel("Net Pay:");
		projectBasedPanel.add(label, "cell 0 4,alignx left");

		projectBasedPanel.add(tfNetPay, "cell 1 4,growx");
		projectBasedPanel.add(PBCalculateBtn, "cell 0 12 2 1,growx");
		return projectBasedPanel;
	}

	private JPanel partTimePane() {
		JPanel partTimePanel = new JPanel(
				new MigLayout("fill,insets 10", "[95px][86px,grow]", "[][][][][][][][][20px][]"));
		partTimePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JLabel partTimerSalaryTItle = new JLabel("Part Timer Salary");
		partTimerSalaryTItle.setFont(new Font("SansSerif", Font.BOLD, 20));

		partTimePanel.add(partTimerSalaryTItle, "cell 0 0 2 1");

		partTimePanel.add(new JLabel("Gross Pay:"), "cell 0 1,alignx left");

		partTimePanel.add(tfGrossPay, "cell 1 1,growx");

		partTimePanel.add(new JLabel("Rate per Day"), "cell 0 2,alignx left");

		partTimePanel.add(tfRatePerHour, "cell 1 2,growx");

		partTimePanel.add(new JLabel("Days Worked"), "cell 0 3,alignx left");

		partTimePanel.add(tfDaysWorked, "cell 1 3,growx");

		partTimePanel.add(new JLabel("Net Pay:"), "cell 0 4,alignx left");

		partTimePanel.add(tfNetPay, "cell 1 4,growx");

		JButton partTimeBtnCalculate = new JButton("Calculate Part Time");
		partTimeBtnCalculate.addActionListener(e -> partTimeMethodCalculation());
		partTimePanel.add(partTimeBtnCalculate, "cell 0 8 2 1,growx");
		return partTimePanel;
	}

	private JPanel deductionPane() {
		JPanel deductionPanel = new JPanel(new MigLayout("fill,insets 10", "[95px][86px]", "[][][][][][][][]"));
		deductionPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		// Deductions
		JLabel lblDeductions = new JLabel("Deductions");
		deductionPanel.add(lblDeductions, "cell 0 0 2 1");
		lblDeductions.setFont(new Font("SansSerif", Font.BOLD, 20));

		JLabel lblSSS = new JLabel("SSS:");
		deductionPanel.add(lblSSS, "cell 0 1");
		tfDeductionsSSS = new JTextField();
		deductionPanel.add(tfDeductionsSSS, "cell 1 1,growx");

		JLabel lblPagIbig = new JLabel("PAG-Ibig:");
		deductionPanel.add(lblPagIbig, "cell 0 2");
		tfDeductionsPagIbig = new JTextField();
		deductionPanel.add(tfDeductionsPagIbig, "cell 1 2,growx");

		JLabel lblPhilHealth = new JLabel("PhilHealth:");
		deductionPanel.add(lblPhilHealth, "cell 0 3");
		tfDeductionsPhilHealth = new JTextField();
		deductionPanel.add(tfDeductionsPhilHealth, "cell 1 3,growx");

		JLabel lblAdvanced = new JLabel("Advanced:");
		deductionPanel.add(lblAdvanced, "cell 0 5");
		tfAdvanced = new JTextField();
		deductionPanel.add(tfAdvanced, "cell 1 5,growx");

		JLabel lblTotalDeductions = new JLabel("Total Deductions:");
		deductionPanel.add(lblTotalDeductions, "cell 0 6");
		tfTotalDeductions = new JTextField();
		deductionPanel.add(tfTotalDeductions, "cell 1 6,growx");
		return deductionPanel;
	}

	private void projectBasedMethodCalculation() {

		double advanced = Double.parseDouble(tfAdvanced.getText());
		double sss = Double.parseDouble(tfDeductionsSSS.getText());
		double pagIbig = Double.parseDouble(tfDeductionsPagIbig.getText());
		double philHealth = Double.parseDouble(tfDeductionsPhilHealth.getText());
		double ratePerProject = Double.parseDouble(PBRatePerProjectField.getText());
		int numberOfProjectsCompleted = Integer.parseInt(PBNumOfProjectsField.getText());

		double totalDeductions = sss + pagIbig + philHealth + advanced;
		double netPay = grossPay - totalDeductions;

		// Update the form with calculated values
		tfNetPay.setText(String.format("%.2f", netPay));

		// Save the payroll record to the database
		// db.insertPayroll(employeeId, employeeName, employeeDepartment,
		// employeeWorkType, grossPay, ratePerProject,
		// 0, 0, 0, totalDeductions, netPay, new Date(System.currentTimeMillis()));
	}

	private void fullTimeMethodCalculation() {
		try {
			String employeeId = tfEmployeeID.getText();
			String employeeName = tfEmployeeName.getText();
			String employeeDepartment = tfEmployeeDepartment.getText();
			String employeeWorkType = tfEmployeeWorkType.getText();
			double ratePerHour = parseDouble(tfRatePerHour.getText());
			int daysWorked = parseInt(tfDaysWorked.getText());
			System.out.println("Days Worked: " + daysWorked);

			double overtimeHours = parseDouble(tfOvertimeHours.getText());
			double advanced = parseDouble(tfAdvanced.getText().replace("%", "")) / 100;
			double bonus = parseDouble(tfBonus.getText());

			double sss = parseDouble(tfDeductionsSSS.getText().replace("%", "")) / 100;
			double pagIbig = parseDouble(tfDeductionsPagIbig.getText().replace("%", "")) / 100;
			double philHealth = parseDouble(tfDeductionsPhilHealth.getText().replace("%", "")) / 100;

			double grossPayField = parseDouble(tfGrossPay.getText());
			System.out.println("SSS: " + sss);
			System.out.println("PagIbig: " + pagIbig);
			System.out.println("PhilHealth: " + philHealth);
			System.out.println("Advanced: " + advanced);

			// Calculate total deductions
			double deductions = sss + pagIbig + philHealth + advanced;
			System.out.println(
					"Deductions (sss + pagIbig + philHealth + advanced): " + String.format("%.2f", deductions));

			System.out.println("Gross Pay: " + grossPayField);

			double overAllDeduction = grossPayField * deductions;
			System.out.println("Overall Deductions: " + String.format("%.2f", overAllDeduction));
			// Calculate net pay
			double netPay = grossPayField + bonus - overAllDeduction;
			System.out.println("Net Pay: " + netPay);

			tfTotalDeductions.setText(String.format("%.2f", deductions * 100) + "%");
			tfNetPay.setText(String.format("%.2f", netPay));

			// Save the payroll record to the database
			db.insertPayroll(employeeId, employeeName, employeeDepartment, employeeWorkType, grossPayField, ratePerHour,
					daysWorked, (int) overtimeHours, bonus, overAllDeduction, netPay,
					new Date(System.currentTimeMillis()));
			refreshTable(payrollHistoryTableModel); // Refresh the payroll history table

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Please ensure all fields are filled correctly.", "Input Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private double parseDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return 0.0; // Default value or consider throwing an exception or showing an error
		}
	}

	private int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0; // Default value or consider throwing an exception or showing an error
		}
	}

	private void partTimeMethodCalculation() {
		try {
			// ratedouble basicSalary = (ratePerHour * 9) * daysWorked;
			double ratePerHour = Double.parseDouble(tfRatePerHour.getText());
			int daysWorked = Integer.parseInt(tfDaysWorked.getText());
			// Calculation
			double basicSalary = (ratePerHour * 5) * daysWorked;
			double grossPay = basicSalary;
			double netPay = grossPay;

			// Update the form with calculated values
			tfNetPay.setText(String.format("%.2f", netPay));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Please ensure all fields are filled correctly.", "Input Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// Save the payroll record to the database
	// db.insertPayroll(employeeId, employeeName, employeeDepartment,
	// employeeWorkType, grossPay, ratePerHour,
	// daysWorked, 0, 0, 0, netPay, new Date(System.currentTimeMillis()));

	private void refreshTable(DefaultTableModel tableModel) {
		// Clear the existing data
		tableModel.setRowCount(0);
	
		// Fetch the updated payroll data from the database
		List<Payroll> payrolls = db.getAllPayrolls();
	
		// Add the new data to the table model
		for (Payroll payroll : payrolls) {
			tableModel.addRow(new Object[]{
				payroll.getEmployeeId(),
				payroll.getEmployeeName(),
				payroll.getEmployeeDepartment(),
				payroll.getEmployeeWorkType(),
				payroll.getGrossPay(),
				payroll.getRatePerHour(),
				payroll.getDaysWorked(),
				payroll.getOvertimeHours(),
				payroll.getBonus(),
				payroll.getTotalDeductions(),
				payroll.getNetPay(),
				payroll.getDate()
			});
		}
	}
	
	private void clearFields() {
		tfEmployeeID.setText("");
		tfEmployeeName.setText("");
		tfEmployeeDepartment.setText("");
		tfEmployeeWorkType.setText("");
		tfRatePerHour.setText("");
		tfDaysWorked.setText("");
		tfOvertimeHours.setText("");
		tfEmpType.setText("");
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
			selectedEmployee = employees.get(row);
			System.out.println("Selected Employee: " + selectedEmployee.getEmployeeId());
			tfEmployeeID.setText(String.valueOf(selectedEmployee.getEmployeeId()));
			tfEmployeeName.setText(selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName());
			tfEmployeeDepartment.setText(selectedEmployee.getDepartment());
			tfEmployeeWorkType.setText(selectedEmployee.getWorkType());
			tfEmpType.setText(selectedEmployee.getEmploymentType());
			tfRatePerHour.setText(String.valueOf(selectedEmployee.getRatePerHour()));

			// Fetch and display days worked and overtime hours
			int daysWorked = db.countDaysWorked(selectedEmployee.getEmployeeId());
			int overtimeHours = db.sumOvertimeHours(selectedEmployee.getEmployeeId());
			tfDaysWorked.setText(String.valueOf(daysWorked));
			tfOvertimeHours.setText(String.valueOf(overtimeHours));

			loadDeductions();
			displayGrossPay();
			updateSalaryPane();
			revalidate();
			repaint();
		}
	}

	private double grossPay;

	private void displayGrossPay() {
		if (selectedEmployee != null) {
			Date date = Date.valueOf(LocalDate.now()); // Example: today's date, adjust as necessary
			double grossPay = db.getGrossPayForEmployee(selectedEmployee.getEmployeeId(), date,
					selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName());
			System.out.println("Fetched Gross Pay: " + grossPay); // Debug statement
			tfGrossPay.setText(String.valueOf(grossPay));
			selectedEmployee.setGrossPay(grossPay);
			tfGrossPay.revalidate();
			tfGrossPay.repaint();
		} else {
			JOptionPane.showMessageDialog(null, "No employee selected.");
		}
	}

	private void updateSalaryPane() {
		salaryPanel.removeAll();
		salaryPanel.setLayout(new BorderLayout()); // Assuming BorderLayout is suitable for your layout needs
		salaryPanel.add(salaryPane(), BorderLayout.CENTER);
		salaryPanel.revalidate();
		salaryPanel.repaint();
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
					EMPListModel.addRow(new Object[] { employee.getEmployeeId(), employee.getFirstName(),
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
				double sss = rs.getDouble("SSS");
				double pagIbig = rs.getDouble("PAG_IBIG");
				double philHealth = rs.getDouble("PHILHEALTH");
				double advanced = rs.getDouble("advanced");

				// Convert absolute values to percentage strings
				String sssPercentage = String.format("%.2f%%", sss * 100);
				String pagIbigPercentage = String.format("%.2f%%", pagIbig * 100);
				String philHealthPercentage = String.format("%.2f%%", philHealth * 100);
				String advancedPercentage = advanced != 0 ? String.format("%.2f%%", advanced * 100) : "0.00%";

				tfDeductionsSSS.setText(sssPercentage);
				tfDeductionsPagIbig.setText(pagIbigPercentage);
				tfDeductionsPhilHealth.setText(philHealthPercentage);
				tfAdvanced.setText(advancedPercentage);
				rs.close();
			}
		} catch (SQLException e) {
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
		g2d.drawString("Daily Rate: " + tfRatePerHour.getText(), x + 10, y + 145);
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
