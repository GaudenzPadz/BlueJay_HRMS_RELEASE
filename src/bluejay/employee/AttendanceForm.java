package bluejay.employee;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import bluejay.Employee;
import bluejay.Main;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class AttendanceForm extends JPanel {

	private static final long serialVersionUID = 1L;
	private Employee employee;
	JComboBox<String> AmPmCombo;
	private String[] column = { "Name", "Date", "Status", "Time In", "Time In Note", "Time Out", "Time Out Note",
			"Overtime", "Worked Hours", "Salary of That Day" }; // add a actions button (like details) to the table

	private DefaultTableModel attendanceModel = new DefaultTableModel(column, 0) {
		private static final long serialVersionUID = 7292205409469675412L;

		@Override
		public boolean isCellEditable(int row, int column) {
			// all cells false
			return false;
		}
	};

	private JTable attendanceTable;
	private EmployeeDatabase db;
	private String currentDate = LocalDate.now().toString(); // Current date as a string
	private JSpinner INhourField;
	private JSpinner INminutesField;
	private JComboBox<String> AmPmOUTCombo;
	private JTextArea clockINnote;
	private JTextArea clockOUTnote;
	private JSpinner hourOUTField;
	private JSpinner minutesOUTField;
	private String name;
	private JTextField grossPayField;

	public AttendanceForm(Employee employee, EmployeeDatabase DB) {
		this.employee = employee;
		name = employee.getFirstName() + " " + employee.getLastName();
		this.db = DB;
		attendanceTable = new JTable(attendanceModel);

		refreshTable(attendanceModel);
		setupUI(shiftEndedPanel());

		ifClockedIN();

	}

	private List<LocalDate> getMissingDates(LocalDate lastDate, LocalDate currentDate) {
		List<LocalDate> missingDates = new ArrayList<>();
		long daysBetween = ChronoUnit.DAYS.between(lastDate, currentDate);
		for (int i = 1; i < daysBetween; i++) {
			missingDates.add(lastDate.plusDays(i));
		}
		return missingDates;
	}

	private void ifClockedIN() {
		LocalDate lastDate = db.getLastClockInDate(employee.getEmployeeId());
		LocalDate currentDate = LocalDate.now();

		// Check for missing dates and mark them as absent
		if (lastDate != null && !lastDate.isEqual(currentDate)) {
			List<LocalDate> missingDates = getMissingDates(lastDate, currentDate);
			markAbsentDays(missingDates, employee.getEmployeeId(), name);
		}

		String attendanceStatus = db.checkAttendanceStatus(employee, employee.getEmployeeId(), name,
				currentDate.toString());

		FlatAnimatedLafChange.showSnapshot();
		removeAll();

		switch (attendanceStatus) {
			case "shiftEnded":
				setupUI(shiftEndedPanel());
				updateGrossPayField(); // Update the gross pay field whenever the attendance status is checked

				break;
			case "noTimeOut":
				setupUI(attendanceOUTForm());

				break;
			default:
				setupUI(attendanceForm());
				break;
		}
		revalidate();
		repaint();
		FlatAnimatedLafChange.hideSnapshotWithAnimation();
	}

	private void markAbsentDays(List<LocalDate> missingDates, String employeeId, String employeeName) {
		for (LocalDate date : missingDates) {
			db.insertAbsentRecordWithNoTime(employeeId, employeeName, date.toString());
		}
	}

	private boolean validateTime(String hoursStr, String minutesStr) {
		try {
			int hours = Integer.parseInt(hoursStr);
			int minutes = Integer.parseInt(minutesStr);
			if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
				JOptionPane.showMessageDialog(this, "Please enter a valid time (Hours: 0-23, Minutes: 0-59).",
						"Invalid Time", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Please enter numeric values for hours and minutes.", "Invalid Input",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private void setupUI(JPanel form) {
		// Set layout
		setLayout(new BorderLayout());

		// Header Panel
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(0, 191, 255).darker());
		headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		headerPanel.setLayout(new MigLayout("", "[left][][grow]", "[50px,grow]"));
		ImageIcon backIcon = new ImageIcon(getClass().getResource("/images/back.png"));

		JButton btnNewButton = new JButton("");
		btnNewButton.setOpaque(false);
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setBorderPainted(false);
		btnNewButton.setIcon(new ImageIcon(backIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH)));
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.frame.replaceContentPane("Weld Well HRMS", new EmployeePanel(employee, db), getLayout());
			}
		});

		headerPanel.add(btnNewButton, "cell 0 0");

		JLabel lblNewLabel = new JLabel("Attendance Form");
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		headerPanel.add(lblNewLabel, "cell 1 0");
		add(headerPanel, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setBackground(null);
		headerPanel.add(panel, "cell 2 0,grow");

		JPanel mainPanel = new JPanel(new MigLayout("fill,insets 20", "[center]", "[center][center]"));
		add(mainPanel, BorderLayout.CENTER);
		JPanel attendanceform = form;

		mainPanel.add(attendanceform, "cell 0 0,alignx center,aligny center");

		JPanel attTable = attendanceTable();
		mainPanel.add(attTable, "cell 0 1,growx");

	}

	private void setCurrentTimeToFields(JSpinner hourSpinner, JSpinner minuteSpinner) {
		Calendar now = Calendar.getInstance();
		int hours = now.get(Calendar.HOUR) == 0 ? 12 : now.get(Calendar.HOUR); // Adjust for 12-hour format
		int minutes = now.get(Calendar.MINUTE);
		boolean isPM = now.get(Calendar.AM_PM) == Calendar.PM;

		hourSpinner.setValue(hours);
		minuteSpinner.setValue(minutes);
		if (this.AmPmCombo == null) {
			AmPmOUTCombo.setSelectedItem(isPM ? "PM" : "AM");
		} else if (this.AmPmOUTCombo == null) {
			AmPmCombo.setSelectedItem(isPM ? "PM" : "AM");
		}
	}

	private JPanel attendanceForm() {
		JPanel attendanceForm = new JPanel(
				new MigLayout("wrap,fillx,insets 25 35 25 35", "[200px,grow,center][100px][200px]",
						"[pref!,center][pref!][pref!][pref!][pref!][pref!][pref!][pref!]"));
		attendanceForm.putClientProperty(FlatClientProperties.STYLE,
				"arc:20;" + "[light]background:darken(@background,3%);" + "[dark]background:lighten(@background,3%)");

		// Time In Label
		JLabel lblTimeIn = new JLabel("Time In");
		attendanceForm.add(lblTimeIn, "cell 0 0,alignx left");

		JLabel noteLabel = new JLabel("Note");
		attendanceForm.add(noteLabel, "cell 2 0,alignx center,aligny center");

		// Hours Field
		INhourField = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
		attendanceForm.add(INhourField, "flowx,cell 0 1,alignx left");

		// Colon Label
		JLabel colon = new JLabel(":");
		attendanceForm.add(colon, "cell 0 1");

		// Minutes Field
		INminutesField = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		attendanceForm.add(INminutesField, "cell 0 1");

		// AM/PM ComboBox
		AmPmCombo = new JComboBox<>(new String[] { "AM", "PM" });
		attendanceForm.add(AmPmCombo, "cell 0 1");

		// Set current time to Time In fields
		setCurrentTimeToFields(INhourField, INminutesField);

		clockINnote = new JTextArea();
		clockINnote.setLineWrap(true);
		attendanceForm.add(clockINnote, "cell 2 1 1 6,grow");

		// Expected Time Out Label
		JLabel errorLabel = new JLabel("\r\n");
		errorLabel.setForeground(Color.RED);
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		attendanceForm.add(errorLabel, "cell 0 2,grow");

		JButton timeINBtn = new JButton("Time In");
		timeINBtn.addActionListener(e -> handleClockIn());
		attendanceForm.add(timeINBtn, "cell 1 7,growx");

		return attendanceForm;
	}

	private void handleClockIn() {
		if (validateTime(INhourField.getValue().toString(), INminutesField.getValue().toString())) {
			int hours = (Integer) INhourField.getValue();
			int minutes = (Integer) INminutesField.getValue();
			String ampm = (String) AmPmCombo.getSelectedItem();
			if (ampm.equals("PM") && hours < 12) {
				hours += 12;
			} else if (ampm.equals("AM") && hours == 12) {
				hours = 0; // Midnight is 0
			}

			ZoneId zoneId = ZoneId.systemDefault();
			LocalDateTime dateTime = LocalDate.now(zoneId).atTime(hours, minutes);
			long unixTimestamp = dateTime.atZone(zoneId).toEpochSecond();

			db.addTimeIn(employee.getEmployeeId(), name, employee.getEmploymentType(), "Active", currentDate,
					unixTimestamp,
					clockINnote.getText());

			employee.setTimeIn(dateTime);

			JOptionPane.showMessageDialog(this, "Time In recorded.");
			refreshTable(attendanceModel);
			removeAll();
			ifClockedIN();
			revalidate();
		}
	}

	private JPanel attendanceOUTForm() {
		JPanel attendanceOUTForm = new JPanel(
				new MigLayout("wrap,fillx,insets 25 35 25 35", "[200px,grow,center][100px][200px]",
						"[pref!,center][pref!][pref!][pref!][pref!][pref!][pref!][pref!][pref!,grow]"));
		attendanceOUTForm.putClientProperty(FlatClientProperties.STYLE,
				"arc:20;" + "[light]background:darken(@background,3%);" + "[dark]background:lighten(@background,3%)");

		attendanceOUTForm.add(new JLabel("Time Out"), "cell 0 0,alignx left");

		attendanceOUTForm.add(new JLabel("Note"), "cell 2 0,alignx center,aligny center");

		// Hours Field
		hourOUTField = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
		attendanceOUTForm.add(hourOUTField, "flowx,cell 0 1,alignx left");

		attendanceOUTForm.add(new JLabel(":"), "cell 0 1");

		// Minutes Field
		minutesOUTField = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		attendanceOUTForm.add(minutesOUTField, "cell 0 1");

		// AM/PM ComboBox
		AmPmOUTCombo = new JComboBox<>(new String[] { "AM", "PM" });
		attendanceOUTForm.add(AmPmOUTCombo, "cell 0 1");

		// Set current time to Time In fields
		setCurrentTimeToFields(hourOUTField, minutesOUTField);

		clockOUTnote = new JTextArea();
		clockOUTnote.setLineWrap(true);
		attendanceOUTForm.add(clockOUTnote, "cell 2 1 1 6,grow");

		JButton OUTBtn = new JButton("Time Out");
		OUTBtn.addActionListener(e -> handleClockOut());

		attendanceOUTForm.add(OUTBtn, "cell 1 8,growx");
		return attendanceOUTForm;
	}

	public double calculateTotalGrossPay() {
		double totalGrossPay = 0.0;
		int grossPayColumnIndex = 9; // Assuming the grossPay is in the tenth column

		for (int i = 0; i < attendanceModel.getRowCount(); i++) {
			Object grossPayObj = attendanceModel.getValueAt(i, grossPayColumnIndex);
			if (grossPayObj != null) {
				double grossPay = Double.parseDouble(grossPayObj.toString());
				totalGrossPay += grossPay;
			}
		}
		return totalGrossPay;
	}

	private void updateGrossPayField() {
		employee.setGrossPay(calculateTotalGrossPay());
	
		// // Check if there are 15 rows or 15 days of attendance records
		// if (attendanceModel.getRowCount() == 15) {
			// Check for existing gross pay record with the same date_created
			if (!db.isGrossPayRecordExists(currentDate, employee.getEmployeeId())) {
				db.insertGrossPay(currentDate, employee.getEmployeeId(), name, employee.getGrossPay());
			}
	
		// 	// Remove the 1st to 15th rows in the database if there are 15 rows in attendance history
		// 	if (attendanceModel.getRowCount() == 15) {
		// 		db.deleteAttendanceRecords(employee.getEmployeeId(), 1, 15);
		// 	}
	
		// 	attendanceModel.setRowCount(0); // Clear the table model
		// 	refreshTable(attendanceModel); // Reload the table data
		// }
	
		grossPayField.setText(String.format("%.2f", employee.getGrossPay()));
	}

	private void handleClockOut() {
		if (validateTime(hourOUTField.getValue().toString(), minutesOUTField.getValue().toString())) {
			int hours = (Integer) hourOUTField.getValue();
			int minutes = (Integer) minutesOUTField.getValue();
			String ampm = (String) AmPmOUTCombo.getSelectedItem();
			if (ampm.equals("PM") && hours < 12) {
				hours += 12;
			} else if (ampm.equals("AM") && hours == 12) {
				hours = 0; // Midnight adjustment
			}
			LocalDateTime timeOut = LocalDate.now().atTime(hours, minutes);
			LocalDateTime timeIn = employee.getTimeIN();

			// Check if timeIn is null
			if (timeIn == null) {
				JOptionPane.showMessageDialog(this, "No Time In recorded for today. Cannot record Time Out.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (timeOut.isBefore(timeIn)) { // Crossed to next day
				timeOut = timeOut.plusDays(1);
			}
			long unixTimestampOUT = timeOut.atZone(ZoneId.systemDefault()).toEpochSecond();
			int workedHoursManual = calculateWorkedHours(timeIn, timeOut);
			int overtimeHours = calculateOvertime(workedHoursManual);

			// Calculate salary based on employment type
			double ratePerHour = employee.getRatePerHour();
			double salary = calculateSalaryByEmploymentType(employee.getEmployeeId(), ratePerHour, workedHoursManual);
			System.out.println(salary);

			db.updateTimeOut(employee.getEmployeeId(), LocalDate.now().toString(), unixTimestampOUT, "Shift Ended",
					clockOUTnote.getText(), overtimeHours, workedHoursManual, salary);
			
			JOptionPane.showMessageDialog(this, "Time Out recorded. Total Worked Hours: " + workedHoursManual
					+ ", Overtime Hours: " + overtimeHours + ", Salary: " + salary);
			refreshTable(attendanceModel);
			ifClockedIN();
			revalidate();
		}
	}

	public double calculateSalaryByEmploymentType(String employeeId, double ratePerHour, int hoursWorked) {
		Employee employee = db.getEmployeeById(employeeId);
		if (employee == null || employee.getEmploymentType() == null) {
			System.err.println("Employee or employment type is null for employee ID: " + employeeId);
			return 0; // or handle appropriately
		}

		String employmentType = employee.getEmploymentType();
		double salary = 0;

		switch (employmentType) {
			case "Full Time":
				salary = employee.calculateFullTime(ratePerHour, hoursWorked);
				System.out.println("Full Time Calculations: " + salary);
				break;
			case "Part Time":
				hoursWorked = Math.min(hoursWorked, 5);
				salary = employee.calculatePartTime(ratePerHour, hoursWorked);
				break;
			case "Project Based":
				// Implementation to be added later
				break;
			default:
				System.out.println("Unknown employment type");
				break;
		}
		return salary;
	}

	private int calculateWorkedHours(LocalDateTime timeIn, LocalDateTime timeOut) {
		long duration = ChronoUnit.SECONDS.between(timeIn, timeOut);
		return (int) (duration / 3600); // Convert seconds to hours
	}

	private int calculateOvertime(int workedHours) {
		int standardWorkdayHours = 9; // Define standard workday hours
		return Math.max(0, workedHours - standardWorkdayHours); // Overtime is any hours worked beyond the stand hours
	}

	private JPanel shiftEndedPanel() {
		JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 20", "[grow]", "[][grow]"));
		JLabel label = new JLabel("Shift Ended. Your attendance has been recorded.");
		label.setFont(new Font("SansSerif", Font.BOLD, 16));
		panel.add(label, "cell 0 0,alignx center");

		return panel;
	}

	private JPanel attendanceTable() {
		// create a JTable
		attendanceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		TableColumnModel columnModel = attendanceTable.getColumnModel();

		columnModel.getColumn(0).setPreferredWidth(170); // name
		columnModel.getColumn(1).setPreferredWidth(100); // date
		columnModel.getColumn(2).setPreferredWidth(100); // status
		columnModel.getColumn(3).setPreferredWidth(100); // Time in
		columnModel.getColumn(4).setPreferredWidth(150); // time in note
		columnModel.getColumn(5).setPreferredWidth(100); // time out
		columnModel.getColumn(6).setPreferredWidth(150); // time out note
		columnModel.getColumn(7).setPreferredWidth(100); // overtime
		columnModel.getColumn(8).setPreferredWidth(150); // worked hours
		columnModel.getColumn(9).setPreferredWidth(150); // "Salary of That Day"

		attendanceTable.setFont(new Font("Serif", Font.PLAIN, 18));
		attendanceTable.setRowHeight(40);
		JScrollPane scrollPane = new JScrollPane(attendanceTable);

		JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 25 35 25 35", "[grow]", "[][grow][grow][]"));

		JLabel lblNewLabel_1 = new JLabel("Attendance History");
		lblNewLabel_1.setFont(new Font("SansSerif", Font.BOLD, 15));
		panel.add(lblNewLabel_1, "cell 0 0,alignx left");
		panel.add(scrollPane, "cell 0 1,grow");

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "cell 0 2,grow");
		panel_1.setLayout(new MigLayout("", "[grow][]", "[][]"));

		JLabel lblNewLabel_2 = new JLabel("Gross Pay: ");
		panel_1.add(lblNewLabel_2, "cell 0 0,alignx right");

		grossPayField = new JTextField();
		panel_1.add(grossPayField, "cell 1 0,growx");
		grossPayField.setColumns(10);

		return panel;
	}

	private void refreshTable(DefaultTableModel modely) {
		modely.setRowCount(0);
		db.loadEMPAttendanceData(employee.getEmployeeId(), modely);
	}

}
