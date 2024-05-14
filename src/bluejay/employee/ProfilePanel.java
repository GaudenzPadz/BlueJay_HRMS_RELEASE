package bluejay.employee;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import com.formdev.flatlaf.FlatClientProperties;

import bluejay.Employee;
import bluejay.Main;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class ProfilePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField fNameField;
	private JTextField mNameField;
	private JTextField lNameField;
	private JTextField workTypeField;
	private JTextField addressField;
	private Image profile; // Store the uploaded image here
	private JPanel imagePanel;
	private EmployeeDatabase db;
	private JTextField IDField;
	private JTextField emailField;
	private JTextField contactNumField;
	private JTextField ageField;
	private JTextField grossPayField;
	private Employee employee;
	private JTextField departmentField;
	private JTextField genderField;
	private JTextField textField;
	private JDatePickerImpl DOBField;

	public ProfilePanel(Employee employee, EmployeeDatabase DB) {
		this.employee = employee;
		this.db = DB;
		setLayout(new BorderLayout());

		// Header Panel
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(0, 191, 255).darker());
		headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		headerPanel.setLayout(new MigLayout("", "[left][]", "[50px,grow]"));

		add(headerPanel, BorderLayout.NORTH);

		ImageIcon backIcon = new ImageIcon(getClass().getResource("/images/back.png"));

		JButton btnNewButton = new JButton("");
		btnNewButton.setOpaque(false);
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setBorderPainted(false);
		btnNewButton.setIcon(new ImageIcon(backIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH)));
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hasModifications()) {
					int choice = JOptionPane.showConfirmDialog(null, "Do you want to save changes?", "Save Changes",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (choice == JOptionPane.YES_OPTION) {
						updateData();
						Main.frame.replaceContentPane("Weld Well HRMS", new EmployeePanel(employee, db), getLayout());
					} else if (choice == JOptionPane.NO_OPTION) {
						Main.frame.replaceContentPane("Weld Well HRMS", new EmployeePanel(employee, db), getLayout());
					}
				} else {
					Main.frame.replaceContentPane("Weld Well HRMS", new EmployeePanel(employee, db), getLayout());
				}
			}
		});

		headerPanel.add(btnNewButton, "cell 0 0");

		JLabel lblNewLabel = new JLabel("Profile");
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		headerPanel.add(lblNewLabel, "cell 1 0");

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("fill,insets 20", "[center][][][][][][][][][][][][]",
				"[center][][][][][][][][][][][][][][][][][]"));

		JLabel lblNewLabel_1 = new JLabel("ID :");
		panel.add(lblNewLabel_1, "flowx,cell 3 0");

		imagePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g); // Ensure the panel is painted correctly

				if (profile != null) { // Check if an image is loaded
					// Draw the image to fit the panel
					g.drawImage(profile, 0, 0, getWidth(), getHeight(), null);
				}
			}
		};
		imagePanel.setBackground(Color.LIGHT_GRAY);
		panel.add(imagePanel, "cell 3 1 1 6,grow");

		JLabel lblNewLabel_2 = new JLabel("First Name");
		panel.add(lblNewLabel_2, "flowx,cell 5 1");

		JLabel Gender = new JLabel("Gender");
		panel.add(Gender, "cell 7 1");

		fNameField = new JTextField();
		panel.add(fNameField, "cell 5 2,grow");
		fNameField.setColumns(10);

		genderField = new JTextField();
		panel.add(genderField, "cell 7 2,growx");
		genderField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Middle Name");
		panel.add(lblNewLabel_3, "cell 5 3");

		JLabel lblNewLabel_7 = new JLabel("Age");
		panel.add(lblNewLabel_7, "cell 7 3");

		mNameField = new JTextField();
		panel.add(mNameField, "cell 5 4,grow");
		mNameField.setColumns(10);

		ageField = new JTextField();
		panel.add(ageField, "cell 7 4,grow");
		ageField.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Surname");
		panel.add(lblNewLabel_4, "cell 5 5");

		lNameField = new JTextField();
		panel.add(lNameField, "cell 5 6,grow");
		lNameField.setColumns(10);

		JButton replacePP = new JButton("Replace Profile Picture");
		panel.add(replacePP, "cell 3 7,alignx center,growy");

		JLabel lblNewLabel_8 = new JLabel("Department");
		panel.add(lblNewLabel_8, "cell 5 7");

		departmentField = new JTextField();
		panel.add(departmentField, "cell 5 8,growx,aligny center");
		departmentField.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel("Work Type");
		panel.add(lblNewLabel_5, "cell 5 9");

		JLabel lblNewLabel_12 = new JLabel("Gross Pay");
		panel.add(lblNewLabel_12, "cell 7 9");

		workTypeField = new JTextField();
		panel.add(workTypeField, "cell 5 10,grow");
		workTypeField.setColumns(10);

		grossPayField = new JTextField();
		panel.add(grossPayField, "cell 7 10,grow");
		grossPayField.setColumns(10);

		JLabel lblNewLabel_6 = new JLabel("Address");
		panel.add(lblNewLabel_6, "cell 5 11");

		addressField = new JTextField();
		panel.add(addressField, "cell 5 12,grow");
		addressField.setColumns(10);

		JLabel lblNewLabel_9 = new JLabel("Email");
		panel.add(lblNewLabel_9, "cell 5 13");

		JLabel dateHiredLabel = new JLabel("Date Hired");
		panel.add(dateHiredLabel, "cell 7 13");

		emailField = new JTextField();
		panel.add(emailField, "cell 5 14,growx");
		emailField.setColumns(10);

		IDField = new JTextField();
		panel.add(IDField, "cell 3 0,alignx left");
		IDField.setColumns(5);

		textField = new JTextField();
		panel.add(textField, "cell 7 14,growx");
		textField.setColumns(10);

		JLabel lblNewLabel_10 = new JLabel("Contact Number");
		panel.add(lblNewLabel_10, "cell 5 15");

		JButton editProfileBtn = new JButton("Edit Profile");
		editProfileBtn.addActionListener((ActionEvent e) -> {
			if (hasModifications()) {
				int choice = JOptionPane.showConfirmDialog(null, "Do you want to save changes?", "Save Changes",
						JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					updateData();
				}
			}			setFieldsEditable(true);
		});
		panel.add(editProfileBtn, "flowx,cell 3 16,alignx left");

		JButton editCredentialsBtn = new JButton("Edit Credentials");
		editCredentialsBtn.addActionListener((ActionEvent e) -> {
			// a dialog to modify the credentials
			EditCredentialsDialog dialog = new EditCredentialsDialog(
					(JFrame) SwingUtilities.getWindowAncestor(ProfilePanel.this), employee, db);

			dialog.setVisible(true);
		});

		contactNumField = new JTextField();
		panel.add(contactNumField, "cell 5 16,growx");
		contactNumField.setColumns(10);
		panel.add(editCredentialsBtn, "cell 3 17");

		// Initialize the date picker for Date of Birth
		SqlDateModel model = new SqlDateModel();
		Properties p = new Properties();
		p.put("text.year", "Year");
		p.put("text.month", "Month");
		p.put("text.today", "Today");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		AbstractFormatter DateLabelFormatter = new AbstractFormatter() {
			private String datePattern = "yyyy-MM-dd";
			private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

			@Override
			public Object stringToValue(String text) throws ParseException {
				return dateFormatter.parseObject(text);
			}

			@Override
			public String valueToString(Object value) throws ParseException {
				if (value != null) {
					Calendar cal = (Calendar) value;
					return dateFormatter.format(cal.getTime());
				}
				return "";
			}
		};
		DOBField = new JDatePickerImpl(datePanel, DateLabelFormatter);
		panel.add(DOBField, "cell 7 6,grow");

		JLabel lblNewLabel_11 = new JLabel("Date of Birth");
		panel.add(lblNewLabel_11, "cell 7 5");
		loadData();

	}

	private int calculateAge(Date dob) {
	    // Convert the Date object to LocalDate
	    LocalDate dateOfBirth = dob.toLocalDate();

	    // Get the current date
	    LocalDate currentDate = LocalDate.now();

	    // Calculate the period between the date of birth and the current date
	    Period period = Period.between(dateOfBirth, currentDate);

	    // Return the years component of the period, which is the age in years
	    return period.getYears();
	}
    
	private void loadData() {
		// Set all text fields to be not editable during the load process
		setFieldsEditable(false);

		// Populate the fields with the employee's data
		IDField.setText(employee.getEmployeeId());
		fNameField.setText(employee.getFirstName());
		mNameField.setText(employee.getMiddleName());
		lNameField.setText(employee.getLastName());
		workTypeField.setText(employee.getWorkType());
		addressField.setText(employee.getAddress());
		emailField.setText(employee.getEmail());
		contactNumField.setText(employee.getContactNumber());
		genderField.setText(employee.getGender());
		departmentField.setText(employee.getDepartment());
        int age = calculateAge( employee.getDOB());
        
		ageField.setText(String.valueOf(age));
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(employee.getDOB());
		DOBField.getModel().setDate(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));

		// To ensure that the date is updated
		DOBField.getModel().setSelected(true);

		// Load the employee's profile image or a default one if none exists
		if (employee.getProfileImage() != null) {
			profile = employee.getProfileImage().getImage();
		} else {
			ImageIcon userIcon = new ImageIcon(getClass().getResource("/images/user.png"));
			profile = userIcon.getImage();
		}

		// Repaint the panel to reflect any image changes
		imagePanel.repaint();
	}

	private boolean hasModifications() {
		// Check if any field has been modified
		return !fNameField.getText().equals(employee.getFirstName()) ||
				!mNameField.getText().equals(employee.getMiddleName()) ||
				!lNameField.getText().equals(employee.getLastName()) ||
				!workTypeField.getText().equals(employee.getWorkType()) ||
				!addressField.getText().equals(employee.getAddress()) ||
				!emailField.getText().equals(employee.getEmail()) ||
				!contactNumField.getText().equals(employee.getContactNumber()) ||
				!DOBField.getModel().getValue().equals(employee.getDOB());
	}

	// Helper method to set all fields editable or not
	private void setFieldsEditable(boolean isEditable) {
		IDField.setEditable(isEditable);
		fNameField.setEditable(isEditable);
		mNameField.setEditable(isEditable);
		lNameField.setEditable(isEditable);
		workTypeField.setEditable(isEditable);
		addressField.setEditable(isEditable);
		emailField.setEditable(isEditable);
		contactNumField.setEditable(isEditable);
		DOBField.getJFormattedTextField().setEditable(false);

	}

	private void updateData() {
		// Get the updated data from the text fields
		employee.setFirstName(fNameField.getText());
		employee.setMiddleName(mNameField.getText());
		employee.setLastName(lNameField.getText());
		employee.setWorkType(workTypeField.getText());
		employee.setAddress(addressField.getText());
		employee.setEmail(emailField.getText());
		employee.setContactNumber(contactNumField.getText());
		// Retrieve the date from the date picker
		Date selectedDate = (Date) DOBField.getModel().getValue();
		java.sql.Date DOB = null;
		if (selectedDate != null) {
			DOB = new java.sql.Date(selectedDate.getTime());
		} else if (DOB == null) {
			JOptionPane.showMessageDialog(null, "Please select a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		employee.setProfileImage(null);

		// Update the employee data in the database
		db.updateEmployee(employee);

		// Reload the data to ensure consistency
		loadData();
	}

}

class EditCredentialsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField oldUsernameField;
	private JTextField newUsernameField;
	private JTextField oldPassField;
	private JTextField newPassField;
	private JTextField confirmPassField;
	private Employee employee;
	private EmployeeDatabase db;
	private JLabel errorToValidateLabel;
	private JLabel newUserLabel;
	private JLabel newPassLabel;
	private JLabel confirmLabel;

	public EditCredentialsDialog(JFrame parent, Employee employee, EmployeeDatabase db) {
		super(parent, true);
		setSize(400, 440);
		setUndecorated(true); // Remove title bar and border
		setLocationRelativeTo(parent);
		this.db = db;
		this.employee = employee;

		JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 20 35 20 35", "[pref!][grow][pref!]",
				"[][][][][][][][][][][][][][][][][]"));
		this.getContentPane().add(panel);
		panel.putClientProperty(FlatClientProperties.STYLE, "" +
				"arc:20;" +
				"[light]background:darken(@background,3%);" +
				"[dark]background:lighten(@background,3%)");
		JLabel lblNewLabel_4 = new JLabel("Edit Credentials");
		lblNewLabel_4.setFont(new Font("SansSerif", Font.BOLD, 15));
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel_4, "cell 1 0,grow");

		JLabel lblNewLabel = new JLabel("Old Username");
		panel.add(lblNewLabel, "cell 1 2");

		oldPassField = new JTextField();
		panel.add(oldPassField, "cell 1 5,growx");
		oldPassField.setColumns(10);

		errorToValidateLabel = new JLabel("");
		errorToValidateLabel.setForeground(Color.RED);
		panel.add(errorToValidateLabel, "flowx,cell 1 6,grow");

		JSeparator separator = new JSeparator();
		panel.add(separator, "cell 1 7,grow");

		newUserLabel = new JLabel("New Username");
		panel.add(newUserLabel, "cell 1 8");

		newUsernameField = new JTextField(10);
		panel.add(newUsernameField, "cell 1 9,growx");

		newPassLabel = new JLabel("New Password");
		panel.add(newPassLabel, "cell 1 10");

		newPassField = new JTextField(10);
		panel.add(newPassField, "cell 1 11,growx");

		confirmLabel = new JLabel("Confirm New Password");
		panel.add(confirmLabel, "cell 1 12");

		confirmPassField = new JTextField(10);
		panel.add(confirmPassField, "cell 1 13,growx");

		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener((ActionEvent e) -> {
			if (hasModifications()) {
				//the fields were modified
				int choice = JOptionPane.showConfirmDialog(null, "Discard Changes?", "",
						JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					dispose();
				}
			} else {
				dispose(); // No modifications, close the dialog
			}
		});

		panel.add(cancelBtn, "flowx,cell 1 15,alignx left");

		JButton saveBtn = new JButton("Save");
		panel.add(saveBtn, "cell 1 15,growx");

		JLabel lblNewLabel_1 = new JLabel("Old Password");
		panel.add(lblNewLabel_1, "cell 1 4");

		oldUsernameField = new JTextField();
		panel.add(oldUsernameField, "cell 1 3,growx");
		oldUsernameField.setColumns(10);

		JButton validateOldLoginBtn = new JButton("Validate");
		validateOldLoginBtn.addActionListener((ActionEvent e) -> {

			String inputUsername = oldUsernameField.getText();
			String inputPassword = oldPassField.getText();
			validateOldLogin(inputUsername, inputPassword);
		});
		panel.add(validateOldLoginBtn, "cell 1 6,alignx right");

		enableNewFields(false);

	}

	private boolean hasModifications() {
		return !newUsernameField.getText().equals(oldUsernameField.getText()) ||
				!newPassField.getText().equals(oldPassField.getText());
	}

	private void updateData() {
		//update changes in the database
		
	}

	public void validateOldLogin(String inputUsername, String inputPassword) {
		String username = inputUsername;
		String password = inputPassword;

		String loginResult = db.validateLogin(username, password);
		System.out.println(loginResult);

		if (loginResult.startsWith("Login successful!")) {
			if (loginResult.contains(employee.getFirstName()) || loginResult.contains(employee.getLastName())) {
				// login validation succesfull
				System.out.println("Validated!");
				enableNewFields(true);

			} else {
				errorToValidateLabel.setText("Invalid username or password");
			}
		} else {
			errorToValidateLabel.setText("Invalid username or password");
		}
	}

	private void enableNewFields(boolean setEnabled) {
		newUsernameField.setEnabled(setEnabled);
		newPassField.setEnabled(setEnabled);
		confirmPassField.setEnabled(setEnabled);
		newPassLabel.setEnabled(setEnabled);
		newUserLabel.setEnabled(setEnabled);
		confirmLabel.setEnabled(setEnabled);
	}

}