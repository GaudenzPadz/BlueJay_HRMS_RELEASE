package bluejayDB;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatClientProperties;

import bluejay.Employee;
import net.miginfocom.swing.MigLayout;

public class EditCredentialsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField oldUsernameField;
	private JTextField oldPassField;
	private JTextField newPassField;
	private JTextField confirmPassField;
	private Employee employee;
	private EmployeeDatabase db;
	private JTextField newUsernameField;
	public EditCredentialsDialog(JFrame parent, Employee employee, EmployeeDatabase db) {
		super(parent, true);
		setSize(400, 340);
		setUndecorated(true); // Remove title bar and border
		setLocationRelativeTo(parent);
		this.db = db;
		this.employee = employee;
		
		JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 20 35 20 35", "[pref!][grow][pref!]", "[][][][][][][][][][][][][][][][][]"));
		this.getContentPane().add(panel);
		panel.putClientProperty(FlatClientProperties.STYLE, "" +
	                "arc:20;" +
	                "[light]background:darken(@background,3%);" +
	                "[dark]background:lighten(@background,3%)");
		JLabel titleLabel = new JLabel("Edit Credentials");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(titleLabel, "cell 1 0,grow");
		
		JLabel lblNewLabel = new JLabel("Old Username");
		panel.add(lblNewLabel, "cell 1 2");
		
		oldPassField = new JTextField();
		panel.add(oldPassField, "cell 1 5,growx");
		oldPassField.setColumns(10);
		
		JLabel errorToValidateLabel = new JLabel("error");
		errorToValidateLabel.setForeground(Color.RED);
		panel.add(errorToValidateLabel, "flowx,cell 1 6,grow");
		
		JSeparator separator = new JSeparator();
		panel.add(separator, "cell 1 7,grow");
		
		JLabel newUserLabel = new JLabel("New Username");
		panel.add(newUserLabel, "cell 1 8");
		
		newUsernameField = new JTextField();
		panel.add(newUsernameField, "cell 1 9,growx");
		newUsernameField.setColumns(10);
		
		JLabel newPassLabel = new JLabel("New Password");
		panel.add(newPassLabel, "cell 1 10");
		
		newPassField = new JTextField();
		panel.add(newPassField, "cell 1 11,growx");
		newPassField.setColumns(10);
		
		JLabel confirmLabel = new JLabel("Confirm New Password");
		panel.add(confirmLabel, "cell 1 12");
		
		confirmPassField = new JTextField();
		panel.add(confirmPassField, "cell 1 13,growx");
		confirmPassField.setColumns(10);
		
		JButton cancelBtn = new JButton("Cancel");
		panel.add(cancelBtn, "flowx,cell 1 15,alignx left");
		
		JButton saveBtn = new JButton("Save");
		panel.add(saveBtn, "cell 1 15,growx");
		
		JLabel lblNewLabel_1 = new JLabel("Old Password");
		panel.add(lblNewLabel_1, "cell 1 4");
		
		oldUsernameField = new JTextField();
		panel.add(oldUsernameField, "cell 1 3,growx");
		oldUsernameField.setColumns(10);
		
		JButton validateOldLoginBtn = new JButton("Validate");
		validateOldLoginBtn.addActionListener((ActionEvent e)-> {});
		panel.add(validateOldLoginBtn, "cell 1 6,alignx right");
	}

}

