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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

import bluejay.Employee;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JSplitPane;

public class PayrollPanel extends JPanel implements Printable {
	private static final long serialVersionUID = 1L;
	private JTextField tfEmployeeID, tfEmployeeName, tfEmployeeDepartment, tfEmployeeWorkType, tfRatePerHour,
			tfDaysWorked, tfOvertimeHours, tfGrossPay, tfOvertimeRate, tfDeductionsSSS, tfDeductionsPagIbig,
			tfDeductionsPhilHealth, tfTotalDeductions, tfAdvanced, tfBonus, tfNetPay;
	private JTable payrollHistoryTable;
	private EmployeeDatabase db;
	private List<Employee> employees;
	private JTable EMPListTable;
	private DefaultTableModel EMPListModel = new DefaultTableModel(
			new String[] { "ID", "Name", "Department", "Employment Type", "Work Type", "Select"},
			0);
	private DefaultTableModel payrollHistoryTableModel = new DefaultTableModel(new String[] { "ID", "Name", "Department", "Work Type", "Gross Pay",
				"Rate Pe rDay", "Days Worked", "Overtime Hours", "Bonus", "Total Deductions", "Net Pay" }, 0);
	private JComboBox<String> employmentTypeComboBox;
	private JTextField wageField;
	private JLabel GrossPayLabel;
	private JLabel RatePerHourLabel;
	private JLabel DaysWorkedLabel;
	private JLabel RateLabel;
	private JLabel OvertimeHoursLabel;
	private JLabel OvertimeRateLabel;
	private JLabel BonusLabel;
	private JLabel NetPayLabel;
	private JLabel TotalDeductionsLabel;
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
		tfEmployeeID = new JTextField(4);
		tfEmployeeName = new JTextField();
		tfEmployeeDepartment = new JTextField();
		tfEmployeeWorkType = new JTextField();
		tfRatePerHour = new JTextField();
		tfDaysWorked = new JTextField();
		tfOvertimeHours = new JTextField();
		tfGrossPay = new JTextField();
		tfOvertimeRate = new JTextField();
		tfDeductionsSSS = new JTextField();
		tfDeductionsPagIbig = new JTextField();
		tfDeductionsPhilHealth = new JTextField();
		tfTotalDeductions = new JTextField();
		tfAdvanced = new JTextField();
		tfBonus = new JTextField();
		payrollHistoryTable = new JTable(payrollHistoryTableModel);

		PBRatePerProjectField = new JTextField();

		employmentTypeComboBox = new JComboBox<String>();

		wageField = new JTextField();

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
			EMPListModel.addRow(new Object[] { employee.getId(), employee.getFirstName(), employee.getLastName(),
					employee.getDepartment(), employee.getWorkType(), "Select" });
		}
	}

	private JPanel setupPayrollCalculationPanel() {
		JPanel payrollCalculationPanel = new JPanel(new MigLayout("",
				"[grow,center][100px,grow][100px,grow][50px][100px,grow][100px,grow][50px][100px,grow][100px,grow][50px][grow,center]",
				"[center][][][][][][][grow][grow][][grow][][grow][][100px,grow][center]"));

		// btnCalculate.addActionListener(e -> calculatePayroll());

		JLabel lblPayrollCalculator = new JLabel("Payroll Calculator");
		lblPayrollCalculator.setFont(new Font("SansSerif", Font.BOLD, 20));
		payrollCalculationPanel.add(lblPayrollCalculator, "cell 1 0 2 1");

		// employee information pane
		JPanel empInfoPanel = EmployeeInformationPane();

		payrollCalculationPanel.add(empInfoPanel, "cell 1 2 2 5,grow");

		// salaryPane
		salaryPanel = salaryPane();
		payrollCalculationPanel.add(salaryPanel, "cell 4 2 2 9,grow");

		// deductions pane
		JPanel deductionPanel = deductionPane();
		payrollCalculationPanel.add(deductionPanel, "cell 7 2 2 9,grow");

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(e -> clearFields());

		// // project based panel
		// JPanel projectBasedPanel = projectBasedPane();
		// payrollCalculationPanel.add(projectBasedPanel, "cell 4 11 2 2,grow");

		JButton btnRefresh = new JButton("Refresh");
		payrollCalculationPanel.add(btnRefresh, "flowx,cell 7 11,growx,aligny center");
		payrollCalculationPanel.add(btnClear, "cell 8 11,growx");

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
		payrollCalculationPanel.add(btnPrint, "cell 8 12,growx,aligny center");

		JLabel lblPayrollHistory = new JLabel("Payroll History");
		lblPayrollHistory.setFont(new Font("SansSerif", Font.BOLD, 20));
		payrollCalculationPanel.add(lblPayrollHistory, "cell 1 13 5 1");

		// Table for payroll history
		payrollHistoryTable = new JTable(EMPListModel);
		JScrollPane scrollPane = new JScrollPane(payrollHistoryTable);
		payrollCalculationPanel.add(scrollPane, "cell 0 14 11 1,grow");

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

		empInfoPanel.add(new JLabel("Employee Name:"), "cell 0 1");
		empInfoPanel.add(tfEmployeeName, "cell 1 1,growx");
		tfEmployeeName.setEditable(false);

		empInfoPanel.add(new JLabel("Department:"), "cell 0 2");
		empInfoPanel.add(tfEmployeeDepartment, "cell 1 2,growx");
		tfEmployeeDepartment.setEditable(false);

		empInfoPanel.add(new JLabel("Employment Type:"), "cell 0 3");
		empInfoPanel.add(employmentTypeComboBox, "cell 1 3,growx");

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

		salaryPane.add(projectBasedPane(), BorderLayout.CENTER);

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
				new MigLayout("fill,insets 10", "[95px][86px]", "[][][][][][][][][20px][][]"));
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
		tfDaysWorked = new JTextField();
		fullTimePanel.add(tfDaysWorked, "cell 1 3,growx");

		RateLabel = new JLabel("Rate:");
		fullTimePanel.add(RateLabel, "cell 0 4");

		fullTimePanel.add(wageField, "cell 1 4,growx");

		OvertimeHoursLabel = new JLabel("Overtime Hours:");
		fullTimePanel.add(OvertimeHoursLabel, "cell 0 5");

		fullTimePanel.add(tfOvertimeHours, "cell 1 5,growx");

		OvertimeRateLabel = new JLabel("Overtime Rate");
		fullTimePanel.add(OvertimeRateLabel, "cell 0 6");

		// base the OT rate by the hours of overtime
		// if 2 hours overtime then OTrate = 400
		// 1 hour is == 200 otRate
		fullTimePanel.add(tfOvertimeRate, "cell 1 6,growx");

		BonusLabel = new JLabel("Bonus");
		fullTimePanel.add(BonusLabel, "cell 0 7");

		fullTimePanel.add(tfBonus, "cell 1 7,growx");

		NetPayLabel = new JLabel("Net Pay:");
		fullTimePanel.add(NetPayLabel, "cell 0 8,alignx center");
		tfNetPay = new JTextField();

		fullTimePanel.add(tfNetPay, "cell 1 8,growx");

		JButton fullTimeBtnCalculate = new JButton("Calculate Full Time");
		fullTimePanel.add(fullTimeBtnCalculate, "cell 0 9 2 1,growx");
		populateEmploymentTypeComboBox();

		return fullTimePanel;
	}

	private JPanel projectBasedPane() {
		JPanel projectBasedPanel = new JPanel(
				new MigLayout("fill,insets 10", "[95px,grow][86px,grow]", "[][][][][][][][][20px][]"));
		projectBasedPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JLabel PBSalaryLabel = new JLabel("Project Based Salary");
		PBSalaryLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		projectBasedPanel.add(PBSalaryLabel, "cell 0 0 2 1");

		projectBasedPanel.add(GrossPayLabel, "cell 0 1,alignx trailing");

		projectBasedPanel.add(tfGrossPay, "cell 1 1,growx");

		projectBasedPanel.add(new JLabel("Rate per Project"), "cell 0 2");

		projectBasedPanel.add(PBRatePerProjectField, "cell 1 2,growx");

		projectBasedPanel.add(new JLabel("Number of Projects Completed"), "cell 0 3,alignx left");

		PBNumOfProjectsField = new JTextField();
		projectBasedPanel.add(PBNumOfProjectsField, "cell 1 3,growx");
		PBNumOfProjectsField.setColumns(10);

		TotalDeductionsLabel = new JLabel("Total Deductions:");
		projectBasedPanel.add(TotalDeductionsLabel, "cell 0 4,alignx left");

		PBTotalDeducField = new JTextField();
		projectBasedPanel.add(PBTotalDeducField, "cell 1 4,growx");
		PBTotalDeducField.setColumns(10);

		JLabel lblNewLabel_9 = new JLabel("Net Pay:");
		projectBasedPanel.add(lblNewLabel_9, "cell 0 5,alignx center");

		PBNetPayField = new JTextField();
		projectBasedPanel.add(PBNetPayField, "cell 1 5,growx");
		PBNetPayField.setColumns(10);
		return projectBasedPanel;
	}

	private JPanel partTimePane() {
		JPanel partTimePanel = new JPanel(
				new MigLayout("fill,insets 10", "[95px][86px,grow]", "[][][][][][][][][20px][]"));

		JLabel partTimerSalaryTItle = new JLabel("Part Timer Salary");
		partTimerSalaryTItle.setFont(new Font("SansSerif", Font.BOLD, 20));

		partTimePanel.add(partTimerSalaryTItle, "cell 0 0 2 1");

		JLabel ptGrossPayLabel = new JLabel("Gross Pay:");
		partTimePanel.add(ptGrossPayLabel, "cell 0 1,alignx left");

		PTGrossPayField = new JTextField();
		partTimePanel.add(PTGrossPayField, "cell 1 1,growx");
		PTGrossPayField.setColumns(10);

		JLabel PTratePerDayLabel = new JLabel("Rate per Day");
		partTimePanel.add(PTratePerDayLabel, "cell 0 2,alignx left");

		PTRatePerDayField = new JTextField();
		partTimePanel.add(PTRatePerDayField, "cell 1 2,growx");
		PTRatePerDayField.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Days Worked");
		partTimePanel.add(lblNewLabel_2, "cell 0 3,alignx left");

		PTDaysWorkedField = new JTextField();
		partTimePanel.add(PTDaysWorkedField, "cell 1 3,growx");
		PTDaysWorkedField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Net Pay:");
		partTimePanel.add(lblNewLabel_3, "cell 0 4,alignx center");

		PTNetPayField = new JTextField(10);
		partTimePanel.add(PTNetPayField, "cell 1 4,growx");

		JButton partTimeBtnCalculate = new JButton("Calculate Part Time");
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

	private void populateEmploymentTypeComboBox() {
		try {
			ResultSet rs = db.getEmploymentTypes(); // Fetch employment type data
			employmentTypeComboBox.removeAllItems(); // Clear existing items
			while (rs.next()) {
				String employmentType = rs.getString("type");
				employmentTypeComboBox.addItem(employmentType); // Add to JComboBox
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetching employment types.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private JTextField PTGrossPayField;
	private JTextField PTRatePerDayField;
	private JTextField PTDaysWorkedField;
	private JTextField PTNetPayField;
	private JTextField PBRatePerProjectField;
	private JTextField PBNumOfProjectsField;
	private JTextField PBTotalDeducField;
	private JTextField PBGrossPayField;
	private JTextField PBNetPayField;

	private void projectBasedMethodCalculation() {
		// rate * numbers of completed projects

	}

	private void fullTimeMethodCalculation() {
		// disable project completed
		//
		// Get the employee ID from the form
		int employeeId = Integer.parseInt(tfEmployeeID.getText());
		// Additional form fields
		String employeeName = tfEmployeeName.getText();
		String employeeDepartment = tfEmployeeDepartment.getText();
		String employeeWorkType = tfEmployeeWorkType.getText();
		double ratePerHour = Double.parseDouble(tfRatePerHour.getText());
		int daysWorked = Integer.parseInt(tfDaysWorked.getText());
		double overtimeHours = Double.parseDouble(tfOvertimeHours.getText());
		double advanced = Double.parseDouble(tfAdvanced.getText());
		double bonus = Double.parseDouble(tfBonus.getText());
		String employmentType = employmentTypeComboBox.getSelectedItem().toString();
		// Deductions
		double sss = Double.parseDouble(tfDeductionsSSS.getText());
		double pagIbig = Double.parseDouble(tfDeductionsPagIbig.getText());
		double philHealth = Double.parseDouble(tfDeductionsPhilHealth.getText());
		double basicSalary = ratePerHour * daysWorked;
		double overTimePay = overtimeHours * ratePerHour; // Use the same rate per day
		double grossPay = basicSalary + overTimePay + bonus;

		double totalDeductions = sss + pagIbig + philHealth + advanced;
		double netPay = grossPay - totalDeductions;

		// Update the form with calculated values
		tfGrossPay.setText(String.format("%.2f", grossPay));
		tfTotalDeductions.setText(String.format("%.2f", totalDeductions));
		tfNetPay.setText(String.format("%.2f", netPay));

		// Save the payroll record to the database
		db.insertPayroll(employeeId, employeeName, employeeDepartment, employeeWorkType, grossPay, ratePerHour,
				daysWorked, (int) overtimeHours, bonus, totalDeductions, netPay, new Date(System.currentTimeMillis())); // Use
																														// the
																														// current
																														// date
																														// }
	}

	private void partTimeMethodCalculation() {
		// rate per day * rate per work * 0.5
		//
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
		tfRatePerHour.setText("");
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
			selectedEmployee = employees.get(row);

			tfEmployeeID.setText(String.valueOf(selectedEmployee.getId()));
			tfEmployeeName.setText(selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName());
			tfEmployeeDepartment.setText(selectedEmployee.getDepartment());
			tfEmployeeWorkType.setText(selectedEmployee.getWorkType());
			tfRatePerHour.setText(String.valueOf(selectedEmployee.getRatePerHour()));

			loadDeductions();
			updateSalaryPane(); // Refresh the salary panel to reflect the new selection
			revalidate();
			repaint();
		}
	}

	private void updateSalaryPane() {
		// Assuming you have a reference to the panel that contains the salaryPane
		salaryPanel.removeAll();
		salaryPanel.add(salaryPane(), "cell 4 2 2 9,grow");
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
