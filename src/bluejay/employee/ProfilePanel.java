package bluejay.employee;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatClientProperties;

import bluejay.Employee;
import bluejay.Main;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;
import raven.datetime.component.date.DateEvent;
import raven.datetime.component.date.DatePicker;
import raven.datetime.component.date.DateSelectionListener;

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
	private Employee employee;
	private JTextField departmentField;
	private JTextField genderField;
	private JTextField dateHiredField;
	private JFormattedTextField DOBField;
	private DatePicker DOBPicker;
	private Icon datePickerIcon;

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
				Main.frame.replaceContentPane("Weld Well HRMS", new EmployeePanel(employee, db), getLayout());
			}
		});

		headerPanel.add(btnNewButton, "cell 0 0,growx");

		JLabel lblNewLabel = new JLabel("Profile");
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		headerPanel.add(lblNewLabel, "cell 1 0,growx");

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("fill,insets 20", "[center][][][][][][][98.00][][][][][]",
				"[center][][][][][][][][][][][][][][][][][]"));

		JLabel lblNewLabel_1 = new JLabel("ID :");
		panel.add(lblNewLabel_1, "flowx,cell 3 0,alignx left");

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
		panel.add(lblNewLabel_2, "flowx,cell 5 1,growx");

		JLabel Gender = new JLabel("Gender");
		panel.add(Gender, "cell 7 1,growx");

		fNameField = new JTextField();
		panel.add(fNameField, "cell 5 2,grow");
		fNameField.setColumns(10);

		genderField = new JTextField();
		genderField.setEditable(false);
		panel.add(genderField, "cell 7 2,growx");
		genderField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Middle Name");
		panel.add(lblNewLabel_3, "cell 5 3,growx");

		JLabel lblNewLabel_7 = new JLabel("Age");
		panel.add(lblNewLabel_7, "cell 7 3,growx");

		mNameField = new JTextField();
		panel.add(mNameField, "cell 5 4,grow");
		mNameField.setColumns(10);

		ageField = new JTextField();
		ageField.setEditable(false);
		panel.add(ageField, "cell 7 4,grow");
		ageField.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Surname");
		panel.add(lblNewLabel_4, "cell 5 5,growx");

		lNameField = new JTextField();
		panel.add(lNameField, "cell 5 6,grow");
		lNameField.setColumns(10);

		JButton replacePP = new JButton("Replace Profile Picture");
		panel.add(replacePP, "cell 3 7,grow");

		JLabel lblNewLabel_6 = new JLabel("Address");
		panel.add(lblNewLabel_6, "cell 5 7,growx");

		addressField = new JTextField();
		panel.add(addressField, "cell 5 8,grow");
		addressField.setColumns(10);

		JLabel lblNewLabel_8 = new JLabel("Department");
		panel.add(lblNewLabel_8, "cell 7 8,growx");

		JLabel lblNewLabel_9 = new JLabel("Email");
		panel.add(lblNewLabel_9, "cell 5 9,growx");

		departmentField = new JTextField();
		departmentField.setEditable(false);
		panel.add(departmentField, "flowx,cell 7 9,growx,aligny center");
		departmentField.setColumns(10);

		emailField = new JTextField();
		panel.add(emailField, "cell 5 10,growx");
		emailField.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel("Work Type");
		panel.add(lblNewLabel_5, "cell 7 10,growx");

		JLabel lblNewLabel_10 = new JLabel("Contact Number");
		panel.add(lblNewLabel_10, "cell 5 11,growx");

		workTypeField = new JTextField();
		panel.add(workTypeField, "cell 7 11,grow");
		workTypeField.setColumns(10);

		contactNumField = new JTextField();
		panel.add(contactNumField, "cell 5 12,growx");
		contactNumField.setColumns(10);

		JLabel dateHiredLabel = new JLabel("Date Hired");
		panel.add(dateHiredLabel, "cell 7 13,growx");

		IDField = new JTextField();
		panel.add(IDField, "cell 3 0,growx");

		dateHiredField = new JTextField();
		dateHiredField.setEditable(false);
		panel.add(dateHiredField, "cell 7 14,growx");
		dateHiredField.setColumns(10);

		JButton editProfileBtn = new JButton("Edit Profile");
		editProfileBtn.addActionListener((ActionEvent e) -> {
			if (hasModifications()) {
				int choice = JOptionPane.showConfirmDialog(null, "Do you want to save changes?", "Save Changes",
						JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					updateData();
				}
			}
			setFieldsEditable(true);
		});
		panel.add(editProfileBtn, "flowx,cell 3 16,alignx center");

		JButton editCredentialsBtn = new JButton("Edit Credentials");
		editCredentialsBtn.addActionListener((ActionEvent e) -> {
			// a dialog to modify the credentials
			EditCredentialsDialog dialog = new EditCredentialsDialog(
					(JFrame) SwingUtilities.getWindowAncestor(ProfilePanel.this), employee, db);

			dialog.setVisible(true);
		});
		panel.add(editCredentialsBtn, "cell 3 17,alignx center");

		DOBPicker = new DatePicker();
		DOBPicker.addDateSelectionListener(new DateSelectionListener() {
			@Override
			public void dateSelected(DateEvent dateEvent) {
				DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				if (DOBPicker.getDateSelectionMode() == DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED) {
					LocalDate date = DOBPicker.getSelectedDate();
					if (date != null) {
						System.out.println("date change " + df.format(DOBPicker.getSelectedDate()));
					} else {
						System.out.println("date change to null");
					}
				} else {
					LocalDate dates[] = DOBPicker.getSelectedDateRange();
					if (dates != null) {
						System.out.println("date change " + df.format(dates[0]) + " to " + df.format(dates[1]));
					} else {
						System.out.println("date change to null");
					}
				}
			}
		});

		DOBPicker.now();
		DOBPicker.setUsePanelOption(true);
		DOBPicker.setCloseAfterSelected(true);
		DOBField = new JFormattedTextField();
		DOBPicker.setEditor(DOBField);
		panel.add(DOBField, "cell 7 6,grow");

		JLabel lblNewLabel_11 = new JLabel("Date of Birth");
		panel.add(lblNewLabel_11, "cell 7 5,growx");
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
		DOBPicker.setSelectedDate(employee.getDOB().toLocalDate());
		int age = calculateAge(employee.getDOB());

		ageField.setText(String.valueOf(age));

		Calendar cal = Calendar.getInstance();
		cal.setTime(employee.getDOB());

		// Load the employee's profile image or a default one if none exists
		if (employee.getProfileImage() != null) {
			profile = employee.getProfileImage().getImage();
		} else {
			ImageIcon userIcon = new ImageIcon(getClass().getResource("/images/user.png"));
			profile = userIcon.getImage();
		}

		// Repaint the panel to reflect any image changes
		imagePanel.repaint();

		dateHiredField.setText(employee.getDateHired().toString());
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
				!DOBPicker.getSelectedDate().equals(employee.getDOB().toLocalDate());
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
		Date DOB = Date.valueOf(DOBPicker.getSelectedDate());

		employee.setDOB(DOB);

		// Do not reset the profile image
		// employee.setProfileImage(null);

		// Update the employee data in the database
		db.updateEmployee(employee);
		
		// Reload the data to ensure consistency
		loadData();
	}
}
