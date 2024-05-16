package bluejay.employee;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatClientProperties;

import bluejay.Employee;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class EditCredentialsDialog extends JDialog {

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
				// the fields were modified
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
		saveBtn.addActionListener((ActionEvent e) -> {
			updateCredentials();
		});
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

	private void updateCredentials() {
		String newUsername = newUsernameField.getText();
		String newPassword = newPassField.getText();
		if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
			db.updateCredentials(employee.getEmployeeId(), newUsername, newPassword);
			JOptionPane.showMessageDialog(this, "Credentials updated successfully.", "Update Successful",
						JOptionPane.INFORMATION_MESSAGE);
			setVisible(false);
			dispose();

		} else {
			JOptionPane.showMessageDialog(this, "Username or password cannot be empty.", "Update Error",
					JOptionPane.ERROR_MESSAGE);
		}
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