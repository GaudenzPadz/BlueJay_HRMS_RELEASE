package bluejay.admin;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import bluejay.Employee;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

class EmployeeEditWindow extends JDialog {
	private JTextField fNameField;
	private JTextField mNameField;
	private JTextField lNameField;
	private JTextField addressField;
	private JTextField rateField;
	private JTextField contactNumberField;
	private JTextField emailField;
	private JTextField otherWorkTypeField;
	private JDatePickerImpl DOBField;
	private JRadioButton radioMale;
	private JRadioButton radioFemale;
	private JComboBox<String> workTypeCombobox;
	private JComboBox<String> departmentComboBox;
	private JPanel imagePanel;
	private JButton saveBtn;
	private JButton replaceProfile;
	private EmployeeDatabase db;
	private Map<String, Integer> workTypeWageMap; // Store work type to wage mapping
	private Employee employee; // Reference to the employee being edited
	private Image profile;
	private JPanel panel;
	private JButton btnNewButton;
	private SqlDateModel model;
	private JComboBox<String> employmentTypeComboBox;

	private JTextField employeeIdField;
	private JLabel ratePerDayLabel;
	private JTextField ratePerDayField;

	public EmployeeEditWindow(JFrame parent, Employee emp, EmployeeDatabase db) {
		super(parent, true);
		setSize(740, 611);
		setUndecorated(true); // Remove title bar and border
		setLocationRelativeTo(parent);
		getContentPane().setLayout(new MigLayout("wrap, fillx, insets 25 35 20 35", "[170px,grow]", "[]"));

		this.employee = emp;
		this.db = db;

		initializeComponents(); // Initialize all components
		populateFields(); // Populate fields with employee data
		setupLayout(); // Layout setup
		populateEmploymentTypeComboBox();
		setupActions(); // Set up action listeners
	}

	private void initializeComponents() {
		employeeIdField = new JTextField();
		fNameField = new JTextField();
		mNameField = new JTextField();
		lNameField = new JTextField();
		addressField = new JTextField();
		contactNumberField = new JTextField();
		emailField = new JTextField();
		rateField = new JTextField();
		workTypeCombobox = new JComboBox<>();
		employmentTypeComboBox = new JComboBox<>();

		// Date picker setup
		model = new SqlDateModel();
		Properties properties = new Properties();
		properties.put("text.year", "Year");
		properties.put("text.month", "Month");
		properties.put("text.today", "Today");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
		AbstractFormatter dateFormatter = new AbstractFormatter() {
			@Override
			public Object stringToValue(String text) throws ParseException {
				return new SimpleDateFormat("yyyy-MM-dd").parse(text);
			}

			@Override
			public String valueToString(Object value) throws ParseException {
				if (value != null) {
					Calendar cal = (Calendar) value;
					return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
				}
				return "";
			}
		};
		DOBField = new JDatePickerImpl(datePanel, dateFormatter);

		// Gender radio buttons
		radioMale = new JRadioButton("Male");
		radioFemale = new JRadioButton("Female");
		ButtonGroup genderGroup = new ButtonGroup();
		genderGroup.add(radioMale);
		genderGroup.add(radioFemale);
		departmentComboBox = new JComboBox<>();
		workTypeWageMap = new HashMap<>();

		// Image panel for employee's profile picture
		imagePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (profile != null) { // Draw the profile image if available
					g.drawImage(profile, 0, 0, getWidth(), getHeight(), null);
				}
			}
		};
	}

	private void populateFields() {
		if (employee == null) {
			throw new IllegalArgumentException("Employee cannot be null");
		}

		employeeIdField.setText(employee.getEmployeeId());
		fNameField.setText(employee.getFirstName());
		mNameField.setText(employee.getMiddleName());
		lNameField.setText(employee.getLastName());
		addressField.setText(employee.getAddress());
		contactNumberField.setText(employee.getContactNumber());
		emailField.setText(employee.getEmail());
		employmentTypeComboBox.setSelectedItem(employee.getEmploymentType());

		if ("Male".equals(employee.getGender())) {
			radioMale.setSelected(true);
		} else {
			radioFemale.setSelected(true);
		}

		// Load the profile image or a default one
		profile = employee.getProfileImage() != null ? employee.getProfileImage().getImage() : null;

		// Populate the DOB field if available
		if (employee.getDOB() != null) {
			java.sql.Date dob = new java.sql.Date(employee.getDOB().getTime());
			model.setValue(dob); // Correct way to set value for SqlDateModel
		}
		employmentTypeComboBox.setSelectedItem(employee.getEmploymentType());

		rateField.setText(String.valueOf(employee.getRatePerHour()));

		populateDepartmentComboBox(); // Populate departments
		populateEmploymentTypeComboBox();
		populateWorkTypeWageMap(); // Populate work type to wage map
		populateWorkTypeComboBox(employee.getDepartment()); // Populate work types

		// Set default wage based on selected work type
		updaterateField();
	}

	private void setupLayout() {
		// Add components to the panel in a structured way
		panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "[180px,grow][100px][fill][grow,fill]", "[][][][][][][][][][][][][][][][][][][][][][][][][]"));
		getContentPane().add(panel, "alignx left,growy");
		// Gender panel
		JPanel genderPanel = new JPanel(new MigLayout("insets 0"));
		genderPanel.add(radioMale);
		genderPanel.add(radioFemale);

		// Add components to the main panel
		panel.add(imagePanel, "cell 0 0 1 7,grow");

		panel.add(new JLabel("Employee ID"), "cell 2 1,alignx trailing");

		panel.add(employeeIdField, "cell 3 1,growx");

		panel.add(new JLabel("First Name"), "cell 2 2,alignx left");
		panel.add(fNameField, "cell 3 2");

		panel.add(new JLabel("Middle Name"), "cell 2 3,alignx left");
		panel.add(mNameField, "cell 3 3");

		panel.add(new JLabel("Last Name"), "cell 2 4,alignx left");
		panel.add(lNameField, "cell 3 4");

		panel.add(new JLabel("Address"), "cell 2 5,alignx left");
		panel.add(addressField, "cell 3 5");

		panel.add(new JLabel("Contact Number"), "cell 2 6,alignx left");
		panel.add(contactNumberField, "cell 3 6");
		replaceProfile = new JButton("Replace Picture");

		panel.add(replaceProfile, "cell 0 7,growx");

		panel.add(new JLabel("Email"), "cell 2 7,alignx left");
		panel.add(emailField, "cell 3 7");

		panel.add(new JLabel("Gender"), "cell 2 8,alignx left");
		panel.add(genderPanel, "cell 3 8");

		panel.add(new JLabel("Date of Birth"), "cell 2 9,alignx left");
		panel.add(DOBField, "cell 3 9,growx");

		panel.add(new JLabel("Department"), "cell 2 10,alignx left");
		panel.add(departmentComboBox, "cell 3 10,growx");

		panel.add(new JLabel("Employment Type"), "cell 2 11,alignx trailing");

		panel.add(employmentTypeComboBox, "cell 3 11,growx");

		panel.add(new JLabel("Work Type"), "cell 2 12,alignx left");

		// Work type combobox
		panel.add(workTypeCombobox, "cell 3 12,growx");
		otherWorkTypeField = new JTextField();
		otherWorkTypeField.setEnabled(false);
		panel.add(otherWorkTypeField, "cell 3 13,growx");

		JLabel label_2 = new JLabel("Rate Per Hour");
		panel.add(label_2, "cell 2 14,alignx left");
		panel.add(rateField, "cell 3 14,growx");

		btnNewButton = new JButton("Cancel");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); // Call method to cancel
			}
		});
		
		ratePerDayLabel = new JLabel("Rate per day");
		panel.add(ratePerDayLabel, "cell 2 15,alignx trailing");
		
		ratePerDayField = new JTextField();
		panel.add(ratePerDayField, "cell 3 15,growx");
		ratePerDayField.setColumns(10);
		panel.add(btnNewButton, "cell 0 22,growx");

		// Buttons
		saveBtn = new JButton("Save");
		panel.add(saveBtn, "cell 3 22");
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

	private void setupActions() {
		// Listener for save button
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveEmployee(); // Call method to save employee data
			}
		});

		// Logic for replacing profile picture (example implementation)
		replaceProfile.addActionListener(e -> {
			// Logic to replace the profile picture (e.g., file chooser, image upload)
			JOptionPane.showMessageDialog(null, "Replace Profile action");
		});

		// Listener to update wage field when work type changes
		workTypeCombobox.addActionListener(e -> updaterateField());

		// Listener to populate work types when department changes
		departmentComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedDepartment = (String) departmentComboBox.getSelectedItem();
				populateWorkTypeComboBox(selectedDepartment); // Update work types based on department
			}
		});

		// Enable otherWorkTypeField when "Other" is selected in workTypeCombobox
		workTypeCombobox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedWorkType = (String) workTypeCombobox.getSelectedItem();
				if ("Other".equals(selectedWorkType)) {
					otherWorkTypeField.setEnabled(true); // Enable custom work type field
				} else {
					otherWorkTypeField.setEnabled(false); // Disable if not "Other"
				}
			}
		});
	}

	private void updaterateField() {
		String selectedWorkType = (String) workTypeCombobox.getSelectedItem();
		if (selectedWorkType != null) {
			Integer wage = workTypeWageMap.get(selectedWorkType);
			if (wage != null) {
				rateField.setText(String.valueOf(wage)); // Set the wage value
			} else {
				rateField.setText(""); // If wage not found, clear the field
			}
		}
	}

	private void populateDepartmentComboBox() {
		try {
			ResultSet rs = db.getDepartments();
			departmentComboBox.removeAllItems(); // Clear existing items
			while (rs.next()) {
				String departmentName = rs.getString("department_name");
				departmentComboBox.addItem(departmentName); // Add to JComboBox
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error fetching departments: " + e.getMessage(), "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void populateWorkTypeComboBox(String departmentName) {
		try {
			// Fetch the department ID using the department name
			int departmentId = db.getDepartmentId(departmentName);

			// Fetch work types based on the department ID
			ResultSet rs = db.getWorkTypesByDepartment(departmentId);

			workTypeCombobox.removeAllItems(); // Clear existing items

			while (rs.next()) {
				String workType = rs.getString("work_type");
				workTypeCombobox.addItem(workType); // Add to the ComboBox
			}

			// Optionally add "Other" for custom work types
			workTypeCombobox.addItem("Other");

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error fetching work types: " + e.getMessage(), "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void populateWorkTypeWageMap() {
		try {
			ResultSet rs = db.getTypes();
			while (rs.next()) {
				String workType = rs.getString("work_type");
				int wage = rs.getInt("rate_per_hour");
				workTypeWageMap.put(workType, wage); // Store in the map
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error fetching work types and wages: " + e.getMessage(),
					"Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveEmployee() {
		try {
			// Check for crucial fields
			String errorMessage = null;

			if (fNameField.getText().isEmpty()) {
				errorMessage = "First Name is required.";
			} else if (lNameField.getText().isEmpty()) {
				errorMessage = "Last Name is required.";
			} else if (departmentComboBox.getSelectedItem() == null) {
				errorMessage = "Department is required.";
			}

			// Check work type
			String selectedWorkType = (String) workTypeCombobox.getSelectedItem();
			if (selectedWorkType == null) {
				errorMessage = "Work Type is required.";
			} else if ("Other".equals(selectedWorkType)) {
				if (otherWorkTypeField.getText().isEmpty()) {
					errorMessage = "Please enter a custom work type.";
				}
			}

			if (rateField.getText().isEmpty()) {
				errorMessage = "Wage is required.";
			}

			if (errorMessage != null) {
				JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
				return; // Stop the save process if required fields are invalid
			}

			// If crucial fields are validated, check non-essential fields
			// Ask user if they want to set empty non-crucial fields to default values
			if (addressField.getText().isEmpty() || contactNumberField.getText().isEmpty()
					|| emailField.getText().isEmpty()) {

				int response = JOptionPane.showConfirmDialog(null,
						"Some optional fields are empty. Would you like to set them to default values?",
						"Set Default Values?", JOptionPane.YES_NO_OPTION);

				if (response == JOptionPane.YES_OPTION) {
					if (addressField.getText().isEmpty())
						addressField.setText("");
					if (contactNumberField.getText().isEmpty())
						contactNumberField.setText("");
					if (emailField.getText().isEmpty())
						emailField.setText("");

				}
			}

			// Set employee data
			employee.setFirstName(fNameField.getText());
			employee.setMiddleName(mNameField.getText());
			employee.setLastName(lNameField.getText());
			employee.setAddress(addressField.getText());
			employee.setDepartment((String) departmentComboBox.getSelectedItem());

			if ("Other".equals(selectedWorkType)) {
				employee.setWorkType(otherWorkTypeField.getText()); // Use custom work type
			} else {
				employee.setWorkType(selectedWorkType); // Normal work type
			}

			employee.setRatePerHour(Double.parseDouble(rateField.getText()));
			employee.setGender(radioMale.isSelected() ? "Male" : "Female");
			employee.setContactNumber(contactNumberField.getText());
			employee.setEmail(emailField.getText());

			// Handle DOB
			Date selectedDate = (Date) DOBField.getModel().getValue();
			java.sql.Date DOB = selectedDate != null ? new java.sql.Date(selectedDate.getTime()) : null;

			if (DOB == null) {
				JOptionPane.showMessageDialog(null, "Please select a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
				return; // Stop the save process if DOB is invalid
			}

			employee.setDOB(DOB);

			db.updateEmployee(employee); // Save data in the database

			JOptionPane.showMessageDialog(null, "Employee data updated successfully.");

			setVisible(false); // Hide the dialog window

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid numeric input. Please check the fields.", "Input Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
