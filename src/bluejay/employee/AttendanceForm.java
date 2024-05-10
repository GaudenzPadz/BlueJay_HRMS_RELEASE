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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
	private String[] column = {
			"Name", "Date", "Status", "Time In", "Time In Note", "Time Out", "Time Out Note", "Overtime",
			"Worked Hours", "Salary of That Day" }; // add a actions button (like details) to the table

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

	public AttendanceForm(Employee employee, EmployeeDatabase DB) {
		this.employee = employee;
		name = employee.getFirstName() + " " + employee.getLastName();
		this.db = DB;
		attendanceTable = new JTable(attendanceModel);

		refreshTable(attendanceModel);
		ifClockedIN();

	}

	private void ifClockedIN() {
		String currentDate = LocalDate.now().toString(); // Format is YYYY-MM-DD
		System.out.println(db.hasCheckedIn(employee.getId(), name, currentDate));
		if (db.hasCheckedIn(employee.getId(), name, currentDate)) {
			FlatAnimatedLafChange.showSnapshot();
			removeAll();
			setupUI(attendanceOUTForm());
			revalidate();
			repaint();
		} else {
			removeAll();
			setupUI(attendanceForm());
			revalidate();
			repaint();
		}
		FlatAnimatedLafChange.hideSnapshotWithAnimation();
	}

	private void refreshTable(DefaultTableModel modely) {
		modely.setRowCount(0);
		db.loadEMPAttendanceData(employee.getId(), modely);
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
		headerPanel.setLayout(new MigLayout("", "[left][]", "[50px,grow]"));
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

		JPanel mainPanel = new JPanel(new MigLayout("fill,insets 20", "[center]", "[center][center]"));
		add(mainPanel, BorderLayout.CENTER);
		JPanel attendanceform = form;

		mainPanel.add(attendanceform, "cell 0 0,alignx center,aligny center");

		JPanel attTable = attendanceTable();
		mainPanel.add(attTable, "cell 0 1,growx");

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
			String noteIN = clockINnote.getText();

			// Convert to 24-hour format
			if (ampm.equals("PM") && hours < 12) {
				hours += 12;
			} else if (ampm.equals("AM") && hours == 12) {
				hours = 0; // Midnight is 0
			}

			// Use system's default zone id
			ZoneId zoneId = ZoneId.systemDefault();
			LocalDateTime dateTime = LocalDate.now(zoneId).atTime(hours, minutes);
			long unixTimestamp = dateTime.atZone(zoneId).toEpochSecond();

			System.out.println(unixTimestamp);
			String status = "Active";

			db.addTimeIn(employee.getId(), name, employee.getEmploymentType(), status, currentDate, unixTimestamp, noteIN);
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

	private List<LocalDate> getMissingDates(LocalDate lastDate, LocalDate currentDate) {
		List<LocalDate> missingDates = new ArrayList<>();
		long daysBetween = ChronoUnit.DAYS.between(lastDate, currentDate);
		for (int i = 1; i < daysBetween; i++) {
			missingDates.add(lastDate.plusDays(i));
		}
		return missingDates;
	}

	private void markAbsentDays(List<LocalDate> missingDates, int employeeId, String employeeName) {
		for (LocalDate date : missingDates) {
			db.insertAbsentRecord(employeeId, employeeName, date.toString());
		}
	}

	public double calculateGrossPayFrom1To15() {
		double totalGrossPay = 0.0;
		int dateColumnIndex = 1; // Assuming the date is in the second column
		int grossPayColumnIndex = 9; // Assuming the grossPay is in the tenth column
	
		for (int i = 0; i < attendanceModel.getRowCount(); i++) {
			String dateStr = (String) attendanceModel.getValueAt(i, dateColumnIndex);
			LocalDate date = LocalDate.parse(dateStr);
			if (date.getDayOfMonth() >= 1 && date.getDayOfMonth() <= 15) {
				double grossPay = Double.parseDouble(attendanceModel.getValueAt(i, grossPayColumnIndex).toString());
				totalGrossPay += grossPay;
			}
		}
		return totalGrossPay;
	}

	private void handleClockOut() {
		if (validateTime(hourOUTField.getValue().toString(), minutesOUTField.getValue().toString())) {
			int hours = (Integer) hourOUTField.getValue();
			int minutes = (Integer) minutesOUTField.getValue();
			String ampm = (String) AmPmOUTCombo.getSelectedItem();
			if (ampm.equals("PM") && hours < 12) {
				hours += 12;
			} else if (ampm.equals("AM") && hours == 12) {
				hours = 0;
			}
			ZoneId zoneId = ZoneId.systemDefault();
			LocalDateTime dateTime = LocalDate.now(zoneId).atTime(hours, minutes);
			long unixTimestampOUT = dateTime.atZone(zoneId).toEpochSecond();
			int overtimeHours = calculateOvertime(hours, minutes);
			db.updateTimeOut(employee.getId(), LocalDate.now().toString(), unixTimestampOUT, clockOUTnote.getText(), overtimeHours, calculateWorkedHours(employee.getTimeIN(), dateTime, overtimeHours), employee.getGrossPay());
			JOptionPane.showMessageDialog(this, "Time Out recorded with " + overtimeHours + " hours of overtime.");
			refreshTable(attendanceModel);
			ifClockedIN();
		}
	}

	private int calculateWorkedHours(LocalDateTime timeIn, LocalDateTime timeOut, int overtimeHours) {
		long duration = ChronoUnit.SECONDS.between(timeIn, timeOut);
		int hoursBetween = (int) (duration / 3600); // Convert seconds to hours
		return hoursBetween + overtimeHours;
	}

	private int calculateOvertime(int hours, int minutes) {
		// Assuming work hours are from 8 AM to 5 PM
		LocalDateTime endTime = LocalDate.now().atTime(17, 0); // 5 PM
		LocalDateTime outTime = LocalDate.now().atTime(hours, minutes);
		if (outTime.isAfter(endTime)) {
			long overtimeMinutes = java.time.Duration.between(endTime, outTime).toMinutes();
			return (int) (overtimeMinutes / 60); // Convert minutes to hours
		}
		return 0;
	}

	private JPanel shiftEndedPanel() {
		JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 20", "[grow]", "[][grow]"));
		JLabel label = new JLabel("Shift Ended. Your attendance has been recorded.");
		label.setFont(new Font("SansSerif", Font.BOLD, 16));
		panel.add(label, "cell 0 0,alignx center");
	
		// Reuse the attendance table panel
		panel.add(attendanceTable(), "cell 0 1,grow");
		return panel;
	}

	private JPanel attendanceTable() {
		// create a JTable
		attendanceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		TableColumnModel columnModel = attendanceTable.getColumnModel();

		columnModel.getColumn(0).setPreferredWidth(170);// name
		columnModel.getColumn(1).setPreferredWidth(100);// date
		columnModel.getColumn(2).setPreferredWidth(100);// status
		columnModel.getColumn(3).setPreferredWidth(100);// Time in
		columnModel.getColumn(4).setPreferredWidth(150);// time in note
		columnModel.getColumn(5).setPreferredWidth(100);// time out
		columnModel.getColumn(6).setPreferredWidth(150);// time out note
		columnModel.getColumn(7).setPreferredWidth(100);// overtime
		columnModel.getColumn(8).setPreferredWidth(150);// worked hours
		columnModel.getColumn(9).setPreferredWidth(150);// "Salary of That Day"

		attendanceTable.setFont(new Font("Serif", Font.PLAIN, 18));
		attendanceTable.setRowHeight(40);
		JScrollPane scrollPane = new JScrollPane(attendanceTable);

		JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 25 35 25 35", "[200px,center]", "[][]"));

		JLabel lblNewLabel_1 = new JLabel("Attendance History");
		lblNewLabel_1.setFont(new Font("SansSerif", Font.BOLD, 15));
		panel.add(lblNewLabel_1, "cell 0 0,alignx left");
		panel.add(scrollPane, "cell 0 1,grow");
		return panel;
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

}
