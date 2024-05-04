package bluejay;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatClientProperties;

import bluejay.admin.AdminPanel;
import bluejay.employee.EmployeePanel;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class LoginPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField usernameField;
	private JPanel passwordPanel;
	private EmployeeDatabase DB;

	public LoginPanel(EmployeeDatabase DB) {
		this.DB = DB;
		setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));

		JPanel panel_1 = new JPanel(
				new MigLayout("wrap,fillx,insets 25 35 25 35", "[250px,center]", "[][20.00][][][][][]"));
		panel_1.putClientProperty(FlatClientProperties.STYLE,
				"arc:20;" + "[light]background:darken(@background,3%);" + "[dark]background:lighten(@background,3%)");

		add(panel_1, "cell 0 0,alignx center,aligny center");
		JLabel welcomeLabel = new JLabel("WELD WELL", JLabel.CENTER);
		welcomeLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

		panel_1.add(welcomeLabel, "cell 0 0");

		JLabel subLabel = new JLabel("Human Resource Management System", JLabel.CENTER);
		panel_1.add(subLabel, "cell 0 1,alignx center,aligny top");

		JLabel label = new JLabel("Username :");
		panel_1.add(label, "cell 0 2,alignx left");

		usernameField = new JTextField(10);
		panel_1.add(usernameField, "cell 0 3,grow");

		JLabel label_1 = new JLabel("Password :");
		panel_1.add(label_1, "cell 0 4,alignx left");

		passwordPanel = new JPanel(new BorderLayout(3, 0));
		JPasswordField passwordField = new JPasswordField(); // Initialize password field

		JToggleButton toggleButton = new JToggleButton("Show");
		toggleButton.setHorizontalAlignment(SwingConstants.RIGHT);
		toggleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (toggleButton.isSelected()) {
					passwordField.setEchoChar((char) 0); // Show characters
					toggleButton.setText("Hide");
				} else {
					passwordField.setEchoChar('\u2022'); // Mask characters
					toggleButton.setText("Show");
				}
			}
		});

		passwordPanel.add(passwordField, BorderLayout.CENTER);
		passwordPanel.add(toggleButton, BorderLayout.EAST);
		panel_1.add(passwordPanel, "cell 0 5,growx");

		usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username or email");
		passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

		JButton loginBtn = new JButton("Login");
		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				String pass = new String(passwordField.getPassword());

				processLogin(username, pass);

			}
		});
		panel_1.add(loginBtn, "flowy,cell 0 6,growx");

		JButton forgotBtn = new JButton("Forgot Password");
		forgotBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// epic codes right here!
				JOptionPane.showMessageDialog(null, "Contact I.T. Support", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		panel_1.add(forgotBtn, "cell 0 7,growx");

//		JCheckBox check = new JCheckBox();
//		panel_1.add(check, "cell 0 8");
//		check.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (check.isSelected()) {
//					EventQueue.invokeLater(() -> {
//						Main.frame.isDark(true); // Sets dark mode
//
//					});
//				} else {
//					EventQueue.invokeLater(() -> {
//						Main.frame.isDark(false); // Sets light mode
//
//					});
//				}
//			}
//		});

	}

	public void processLogin(String inputUsername, String inputPassword) {
		String username = inputUsername;
		String password = inputPassword;

		String loginResult = DB.validateLogin(username, password);
		System.out.println(loginResult);

		if (loginResult.startsWith("Login successful!")) {

			if (loginResult.contains("ADMIN")) {
				Main.frame.replaceContentPane("Admin Panel", new AdminPanel(DB), new BorderLayout());

			} else if (loginResult.contains("Employee")) {
				// Extract user ID from login result or another method
				Employee employee = DB.getEmployeeDataByUsername(username);
				if (employee != null) {
					Main.frame.replaceContentPane("Weld Well HRMS", new EmployeePanel(employee, DB), new BorderLayout());

				} else {
					JOptionPane.showMessageDialog(null, "No employee data found for this user");
				}
			} else {
				System.out.println("Unexpected login result format: " + loginResult);
				JOptionPane.showMessageDialog(null, "Invalid username or password");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Invalid username or password");
		}
	}
}