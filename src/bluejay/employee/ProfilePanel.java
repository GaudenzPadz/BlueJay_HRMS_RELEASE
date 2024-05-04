package bluejay.employee;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	private JTextField telNumField;
	private JTextField DOBField;
	private JTextField textField;
	private JTextField textField_1;
	private Employee employee;
	
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
				Main.frame.replaceContentPane("Weld Well HRMS",new EmployeePanel(employee, db), getLayout());
			}
		});

		headerPanel.add(btnNewButton, "cell 0 0");

		JLabel lblNewLabel = new JLabel("Profile");
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		headerPanel.add(lblNewLabel, "cell 1 0");

		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("fill,insets 20", "[center][][][][][][][][][][][][]",
				"[center][][][][][][][][][][][][][][][]"));

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

		fNameField = new JTextField();
		panel.add(fNameField, "cell 5 2,grow");
		fNameField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Middle Name");
		panel.add(lblNewLabel_3, "cell 5 3");

		JLabel lblNewLabel_11 = new JLabel("Date of Birth");
		panel.add(lblNewLabel_11, "cell 7 3");

		mNameField = new JTextField();
		panel.add(mNameField, "cell 5 4,grow");
		mNameField.setColumns(10);

		DOBField = new JTextField();
		panel.add(DOBField, "cell 7 4,grow");
		DOBField.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Surname");
		panel.add(lblNewLabel_4, "cell 5 5");

		JLabel lblNewLabel_7 = new JLabel("Age");
		panel.add(lblNewLabel_7, "cell 7 5");

		lNameField = new JTextField();
		panel.add(lNameField, "cell 5 6,grow");
		lNameField.setColumns(10);

		textField = new JTextField();
		panel.add(textField, "cell 7 6,grow");
		textField.setColumns(10);

		JButton replacePP = new JButton("Replace Profile Picture");
		panel.add(replacePP, "cell 3 7,alignx center,aligny center");

		JLabel lblNewLabel_5 = new JLabel("Work Type");
		panel.add(lblNewLabel_5, "cell 5 7");

		JLabel lblNewLabel_12 = new JLabel("New label");
		panel.add(lblNewLabel_12, "cell 7 7");

		workTypeField = new JTextField();
		panel.add(workTypeField, "cell 5 8,grow");
		workTypeField.setColumns(10);

		textField_1 = new JTextField();
		panel.add(textField_1, "cell 7 8,grow");
		textField_1.setColumns(10);

		JLabel lblNewLabel_6 = new JLabel("Address");
		panel.add(lblNewLabel_6, "cell 5 9");

		addressField = new JTextField();
		panel.add(addressField, "cell 5 10,grow");
		addressField.setColumns(10);

		JLabel lblNewLabel_9 = new JLabel("Email");
		panel.add(lblNewLabel_9, "cell 5 11");

		emailField = new JTextField();
		panel.add(emailField, "cell 5 12,growx");
		emailField.setColumns(10);

		IDField = new JTextField();
		panel.add(IDField, "cell 3 0,alignx left");
		IDField.setColumns(5);

		JLabel lblNewLabel_10 = new JLabel("Telephone Number");
		panel.add(lblNewLabel_10, "cell 5 13");

		JButton editProfileBtn = new JButton("Edit Profile");
		editProfileBtn.addActionListener((ActionEvent e) -> {
			editData();
			
		});
		panel.add(editProfileBtn, "flowx,cell 3 14,alignx left");

		telNumField = new JTextField();
		panel.add(telNumField, "cell 5 14,growx");
		telNumField.setColumns(10);

		JButton editCredentialsBtn = new JButton("Edit Credentials");
		editCredentialsBtn.addActionListener((ActionEvent e)-> {
			//a dialog to modify the credentials 
			
		});
		panel.add(editCredentialsBtn, "cell 3 15");
		loadData();

	}

	private void loadData() {
		// Set all text fields to be not editable during the load process
		setFieldsEditable(false);

		// Populate the fields with the employee's data
		IDField.setText(String.valueOf(employee.getId()));
		fNameField.setText(employee.getFirstName());
		mNameField.setText(employee.getMiddleName());
		lNameField.setText(employee.getLastName());
		workTypeField.setText(employee.getWorkType());
		addressField.setText(employee.getAddress());
		emailField.setText(employee.getEmail());
		telNumField.setText(employee.getTelNumber());
		DOBField.setText(employee.getDOB().toString()); // Assuming it's a date type

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

	// Helper method to set all fields editable or not
	private void setFieldsEditable(boolean isEditable) {
		IDField.setEditable(isEditable);
		fNameField.setEditable(isEditable);
		mNameField.setEditable(isEditable);
		lNameField.setEditable(isEditable);
		workTypeField.setEditable(isEditable);
		addressField.setEditable(isEditable);
		emailField.setEditable(isEditable);
		telNumField.setEditable(isEditable);
		DOBField.setEditable(isEditable);
	}

	private void editData() {
		// Set all fields to be editable
		setFieldsEditable(true);
	}

	private void updateData() {
		// Get the updated data from the text fields
		employee.setFirstName(fNameField.getText());
		employee.setMiddleName(mNameField.getText());
		employee.setLastName(lNameField.getText());
		employee.setWorkType(workTypeField.getText());
		employee.setAddress(addressField.getText());
		employee.setEmail(emailField.getText());
		employee.setTelNUmber(telNumField.getText());
		employee.setDOB(Date.valueOf(DOBField.getText()));

		employee.setProfileImage(null);

		// Update the employee data in the database
		db.updateEmployee(employee);

		// Reload the data to ensure consistency
		loadData();
	}

}
