package bluejay.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import com.formdev.flatlaf.FlatClientProperties;

import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class AddEMPPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField fNameField;
	private JTextField lNameField;
	private JTextField workTypeField;
	private JTextField addressField;
	private JTextField emailField;
	private JTextField telField;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JSeparator separator;
	private JButton btnNewButton;
	private JTextField textField_4;
	private JRadioButton radioMale, radioFemale;

	private JPanel panel;
	private JScrollPane sp;
	private EmployeeDatabase db;
	protected String input;
	private JLabel lblNewLabel_1;
	private JButton uploadBtn;
	private ImageIcon imageIcon; // Store the uploaded image here
	private int newId;
	private JTextField wageField; // Field to display the wage
	private JComboBox<String> workTypeCombobox;
	private Map<String, Integer> workTypeWageMap; // Store work type to wage mapping
	private JDatePickerImpl DOBField;

	public AddEMPPanel(EmployeeDatabase DB) {
		this.db = DB;

		setLayout(new MigLayout("center", "[center]", "[center]"));

		panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "[pref!,grow,fill]",
				"[][][grow][][][][][][][][][][][][][][][][][][][][][][][][][]"));
		sp = new JScrollPane(panel);
		sp.putClientProperty(FlatClientProperties.STYLE,
				"arc:20;" + "[light]background:darken(@background,3%);" + "[dark]background:lighten(@background,3%)");
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(sp, "flowx,cell 0 1,alignx center,aligny center");

		fNameField = new JTextField(20);
		lNameField = new JTextField(20);
		fNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter First Name");
		lNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Last Name");

		ButtonGroup groupGender = new ButtonGroup();

		// Initialize work type to wage mapping
		workTypeWageMap = new HashMap<>();
		populateWorkTypeWageMap(); // Load data from the database into the map

		workTypeCombobox = new JComboBox<>();
		wageField = new JTextField();
		wageField.setEditable(false); // Set as non-editable

		// Add work types to the combobox
		for (String workType : workTypeWageMap.keySet()) {
			workTypeCombobox.addItem(workType);
		}

		// ActionListener to update the wage field when the work type is selected
		workTypeCombobox.addActionListener((ActionEvent e) -> {
			updateWageField(); // Call the method to update the wage
		});

		// Adding a new item for "Other"
		workTypeCombobox.addItem("Other");
		workTypeCombobox.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Work Type");

		// Add ActionListener to JComboBox
		workTypeCombobox.addActionListener((ActionEvent e) -> {
			if ("Other".equals(workTypeCombobox.getSelectedItem())) {
				workTypeField.setEnabled(true); // Enable the JTextField for custom input
				wageField.setEditable(true); // Set as non-editable
			} else {
				workTypeField.setEnabled(false); // Disable if "Other" is not selected
				wageField.setEditable(false); // Set as non-editable
			}
		});

		workTypeField = new JTextField(6);
		workTypeField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Work Type");
		workTypeField.setEnabled(false);
		JLabel label = new JLabel("Address");
		addressField = new JTextField();
		addressField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Address");
		JLabel lblNewLabel_2 = new JLabel("Gender");

		JPanel genderPanel = new JPanel(new MigLayout("insets 0"));
		radioMale = new JRadioButton("Male");
		radioFemale = new JRadioButton("Female");
		groupGender.add(radioMale);
		groupGender.add(radioFemale);
		radioMale.setSelected(true);
		genderPanel.add(radioMale);
		genderPanel.add(radioFemale);

		JLabel lblNewLabel_3 = new JLabel("Telephone Number");
		telField = new JTextField(10);
		telField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Telephone Number");

		uploadPanel = new JPanel();
		panel.add(uploadPanel, "cell 0 0");
		uploadPanel.setLayout(new MigLayout("", "[pref!,grow,fill][100px:150px]", "[100px:150px]"));

		imagePanel = new JPanel(new BorderLayout());
		imageLabel = new JLabel("");
		imagePanel.add(imageLabel, BorderLayout.CENTER); // Center the image

		// Set a fixed size for the imagePanel to maintain a 1x1 aspect ratio
		int imageSize = 150; // Example size
		imagePanel.setPreferredSize(new Dimension(imageSize, imageSize));

		uploadPanel.add(imagePanel, "cell 1 0,grow");

		lblNewLabel_1 = new JLabel("Picture (use 1x1)");
		uploadPanel.add(lblNewLabel_1, "flowx,cell 0 0,alignx center,aligny center");

		uploadBtn = new JButton("Upload");
		uploadPanel.add(uploadBtn, "cell 0 0,aligny center");
		uploadBtn.addActionListener((ActionEvent e) -> {
			uploadImage();
			SwingUtilities.invokeLater(() -> {
			});

		});

		panel.add(new JLabel("Full Name"), "cell 0 1");
		panel.add(fNameField, "flowx,cell 0 2");
		panel.add(lNameField, "cell 0 2");

		panel.add(new JLabel("Department"), "cell 0 3");

		departmentComboBox = new JComboBox();
		populateDepartmentComboBox(); // Populate the JComboBox with department data

		departmentComboBox.addActionListener((ActionEvent e) -> {
			JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
			String selectedDepartment = (String) comboBox.getSelectedItem();

			// Retrieve the department ID from the database
			int departmentId = db.getDepartmentId(selectedDepartment);

			updateWorkTypeComboBox(departmentId); // Update work types based on department
		});

		panel.add(departmentComboBox, "cell 0 4,growx");

		panel.add(new JLabel("Work Type"), "cell 0 5");

		panel.add(workTypeCombobox, "flowx,cell 0 6,alignx right");
		panel.add(workTypeField, "cell 0 6,alignx right");

		lblRate = new JLabel("Wage");
		panel.add(lblRate, "cell 0 7");

		wageField = new JTextField();
		wageField.setEditable(false);
		panel.add(wageField, "cell 0 8,growx");
		wageField.setColumns(10);

		panel.add(label, "cell 0 9");

		panel.add(addressField, "cell 0 10,growx");

		JLabel lblNewLabel_7 = new JLabel("Employee's ID:");
		panel.add(lblNewLabel_7, "flowx,cell 0 11");

		panel.add(lblNewLabel_2, "cell 0 12,gapy 5");

		panel.add(genderPanel, "cell 0 13");

		panel.add(lblNewLabel_3, "cell 0 14");

		panel.add(telField, "flowx,cell 0 15,growx");
		JLabel lblNewLabel_4 = new JLabel("Email");

		panel.add(lblNewLabel_4, "cell 0 16");
		emailField = new JTextField(10);
		emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter email");

		panel.add(emailField, "cell 0 17,growx");

		JLabel lblNewLabel_6 = new JLabel("Date of Birth");
		panel.add(lblNewLabel_6, "cell 0 18");

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
		panel.add(DOBField, "cell 0 19,growx");

		panel.add(DOBField, "cell 0 19,growx");

		panel.add(new JSeparator(), "cell 0 20,gapy 5 5");

		JLabel lblNewLabel = new JLabel("Username");
		panel.add(lblNewLabel, "cell 0 21");

		usernameField = new JTextField(10);
		usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter username");
		panel.add(usernameField, "cell 0 22,growx");

		JLabel lblNewLabel_5 = new JLabel("Password");
		panel.add(lblNewLabel_5, "cell 0 23");

		passwordField = new JPasswordField();
		passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter password");

		panel.add(passwordField, "cell 0 24,growx");
		passwordField.setColumns(10);

		separator = new JSeparator();
		panel.add(separator, "cell 0 25,growx");

		btnNewButton = new JButton("Add Employee");
		btnNewButton.addActionListener(this::addButtonClicked);
		panel.add(btnNewButton, "cell 0 26");

		textField_4 = new JTextField();
		try {
			newId = db.getLastEmployeeId() + 1;
			textField_4.setText(String.valueOf(newId)); // Convert int to String
		} catch (SQLException e) {
			// Handle SQLException here
			// e.printStackTrace(); // For debugging
			JOptionPane.showMessageDialog(null, "An error occurred while retrieving employee ID.");
		}

		textField_4.setEditable(false);
		panel.add(textField_4, "cell 0 11");
		textField_4.setColumns(10);

	}

	private void populateDepartmentComboBox() {
		try {
			ResultSet rs = db.getDepartments(); // Fetch department data
			departmentComboBox.removeAllItems(); // Clear existing items
			while (rs.next()) {
				String departmentName = rs.getString("department_name");
				departmentComboBox.addItem(departmentName); // Add to JComboBox
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetching departments.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateWorkTypeComboBox(int departmentId) {
		try {
			ResultSet rs = db.getWorkTypesByDepartment(departmentId);

			workTypeCombobox.removeAllItems(); // Clear existing items

			while (rs.next()) {
				String workType = rs.getString("work_type");
				workTypeCombobox.addItem(workType); // Add to JComboBox
			}

			// Optionally add "Other" for custom work types
			workTypeCombobox.addItem("Other");
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetching work types.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void populateWorkTypeWageMap() {
		try {
			ResultSet rs = db.getTypes(); // Get data from the database
			while (rs.next()) {
				String workType = rs.getString("work_type");
				int wage = rs.getInt("wage");
				workTypeWageMap.put(workType, wage); // Store in the map
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetching work types and wages.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateWageField() {
		String selectedWorkType = (String) workTypeCombobox.getSelectedItem();
		if (selectedWorkType != null) {
			Integer wage = workTypeWageMap.get(selectedWorkType);
			if (wage != null) {
				wageField.setText(String.valueOf(wage)); // Set the wage value
			} else {
				wageField.setText(""); // No wage found, clear the field
			}
		}
	}

	private File selectedFile;
	private JPanel imagePanel;
	private JPanel uploadPanel;
	private JLabel lblRate;
	private JComboBox<String> departmentComboBox;
	protected JLabel imageLabel;

	private void uploadImage() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
		fileChooser.setFileFilter(filter);

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();

			// Use SwingWorker to process the image in the background
			SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
				@Override
				protected ImageIcon doInBackground() throws Exception {
					// Load the image and create an ImageIcon
					try {
						return new ImageIcon(selectedFile.getAbsolutePath());
					} catch (Exception ex) {
						ex.printStackTrace();
						throw new IOException("Failed to load image.");
					}
				}

				@Override
				protected void done() {
					try {
						imageIcon = get(); // Get the loaded ImageIcon
						// Resize and set the image with a 1x1 aspect ratio
						Image scaledImage = imageIcon.getImage().getScaledInstance(imagePanel.getWidth(),
								imagePanel.getHeight(), Image.SCALE_SMOOTH);
						imageLabel.setIcon(new ImageIcon(scaledImage)); // Set the resized image to the label
						imageLabel.revalidate(); // Refresh the label
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error loading image: " + e.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			};

			worker.execute(); // Start the SwingWorker
		}
	}
	  boolean isValid = true;

	  private boolean validateAndHighlight() {
		    Color errorColor = Color.RED;
		    Border errorBorder = BorderFactory.createLineBorder(errorColor);

		    // Helper lambda to simplify validation
		    BiConsumer<JTextField, String> validateTextField = (field, content) -> {
		        if (content.trim().isEmpty()) {
		            field.setBorder(errorBorder);
		            isValid = false;
		        } else {
		            field.setBorder(UIManager.getBorder("TextField.border"));
		        }
		    };

		    validateTextField.accept(fNameField, fNameField.getText());
		    validateTextField.accept(lNameField, lNameField.getText());
		    validateTextField.accept(addressField, addressField.getText());
		    validateTextField.accept(emailField, emailField.getText());
		    validateTextField.accept(telField, telField.getText());
		    validateTextField.accept(usernameField, usernameField.getText());

		    // Validate phone number with regex and length
		    if (!isValidPhoneNumber(telField.getText())) {
		        telField.setBorder(errorBorder);
		        isValid = false;
		    }

		    // Date of Birth validation
		    if (!isValidDate(DOBField.getJFormattedTextField().getText())) {
		        DOBField.setBorder(errorBorder);
		        isValid = false;
		    }

		    // Image validation
		    if (!isValidImage(selectedFile)) {
		        uploadBtn.setBorder(errorBorder);
		        isValid = false;
		    }

		    // Validate workTypeField only if "Other" is selected
		    if ("Other".equals(workTypeCombobox.getSelectedItem())) {
		        validateTextField.accept(workTypeField, workTypeField.getText());
		    }

		    return isValid;
		}

	private boolean isValidPhoneNumber(String phoneNumber) {
		// This regex allows digits, +, and - characters
		String phoneRegex = "^[\\d+\\-]+$";
		// Define a reasonable range for phone number length
		int minLength = 2;
		int maxLength = 15;

		return phoneNumber.matches(phoneRegex) && phoneNumber.length() >= minLength
				&& phoneNumber.length() <= maxLength;
	}
	
	private boolean isValidImage(File file) {
		if (file == null) {
			return false; // No file selected
		}

		String fileName = file.getName().toLowerCase();

		// Check for reasonable file size (e.g., less than 5 MB)
		long fileSize = file.length();
		long maxFileSize = 5 * 1024 * 1024; // 5 MB in bytes
		if (fileSize > maxFileSize) {
			return false; // File size too large
		}

		return true; // File is valid
	}

	private boolean isValidDate(String dateText) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    sdf.setLenient(false);
	    try {
	        sdf.parse(dateText);
	        return true;
	    } catch (ParseException e) {
	        return false;
	    }
	}
	
	private byte[] fileToByteArray(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			return fis.readAllBytes();
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error reading file. Please try again.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	private void addButtonClicked(ActionEvent e) {
		// Retrieve text from fields

		String firstName = fNameField.getText();
		String lastName = lNameField.getText();
		String address = addressField.getText();
		String selectedDepartment = (String) departmentComboBox.getSelectedItem(); // Get selected department name
		int departmentId = db.getDepartmentId(selectedDepartment); // Get the department ID based on the name

		String selectedWorkType = (String) workTypeCombobox.getSelectedItem();
		int workTypeId = db.getWorkTypeId(selectedWorkType); 
		
		String gender = radioMale.isSelected() ? "Male" : "Female";
		String telNum = telField.getText();
		String email = emailField.getText();
		String username = usernameField.getText();
		String password = new String(passwordField.getPassword());

		// Retrieve the date from the date picker
		Date selectedDate = (Date) DOBField.getModel().getValue();
		java.sql.Date DOB = null;
		if (selectedDate != null) {
			DOB = new java.sql.Date(selectedDate.getTime());
		}

		// Ensure the date is not null before proceeding
		if (DOB == null) {
			JOptionPane.showMessageDialog(null, "Please select a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Ensure required fields are filled
		if (!validateAndHighlight()) {
			JOptionPane.showMessageDialog(null, "Please correct the highlighted fields.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return; // Exit early if validation fails
		}

		try {
			byte[] imageData = fileToByteArray(selectedFile);
			double wage = Double.valueOf(wageField.getText());
		      // Get the current date using LocalDate
	        LocalDate localDate = LocalDate.now();

	        // Convert LocalDate to java.sql.Date
	        Date dateToday = Date.valueOf(localDate);
	        double grossPay = 0;
	        double netPay = 0;
			db.insertEMPData(firstName, lastName, address, workTypeId, wage, gender, telNum, DOB, email, imageData,
					departmentId, dateToday, grossPay,netPay);
			
			db.insertEMPCredentials(firstName + " " + lastName, username, password, "Employee");

			JOptionPane.showMessageDialog(null, "Employee added successfully.", "Success",
					JOptionPane.INFORMATION_MESSAGE);

			// Clear the fields after successful insertion
			fNameField.setText("");
			lNameField.setText("");
			addressField.setText("");
			telField.setText("");
			emailField.setText("");
			DOBField.getModel().setValue(null); // Reset the model value
			workTypeCombobox.setSelectedItem(null);
			usernameField.setText("");
			passwordField.setText("");

			// Clear the image from the profile panel
			imageLabel.setIcon(null); // Remove the image
			selectedFile = null; // Reset the selected file to indicate no image

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "An error occurred while adding the employee. Please try again.",
					"Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	
}