package bluejay.employee;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bluejay.ButtonPanel;
import bluejay.Employee;
import bluejay.LoginPanel;
import bluejay.Main;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class EmployeePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel menuPanel;
	private Employee employee;
	private EmployeeDatabase db;

	public EmployeePanel(Employee employee, EmployeeDatabase DB) {
		this.employee = employee;
		this.db = DB;
		setLayout(new BorderLayout());

		// Header Panel
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(0, 191, 255));
		headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		headerPanel.setLayout(new MigLayout("", "[30px][left][30][][][grow]", "[100px,grow,shrink 70]"));

		// Icon Label
		JLabel iconLabel = new JLabel();
		Main.frame.setScaledLogo(200, 200);
		iconLabel.setIcon(Main.frame.getScaledLogo());
		iconLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		iconLabel.setForeground(Color.WHITE);
		headerPanel.add(iconLabel, "cell 1 0,alignx left,growy");

		// Labels Panel
		JPanel labelsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
		labelsPanel.setOpaque(false);
		JLabel welcomeLabel = new JLabel("Welcome!");
		welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		welcomeLabel.setForeground(Color.WHITE);

		JLabel nameLabel = new JLabel(employee.getFirstName() + " " + employee.getLastName());
		nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
		nameLabel.setForeground(Color.WHITE);

		JLabel deptLabel = new JLabel(employee.getDepartment());
		deptLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
		deptLabel.setForeground(Color.WHITE);

		JLabel workTypeLabel = new JLabel(employee.getWorkType());
		workTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
		workTypeLabel.setForeground(Color.WHITE);

		labelsPanel.add(welcomeLabel);
		labelsPanel.add(nameLabel);
		labelsPanel.add(deptLabel);
		labelsPanel.add(workTypeLabel);
		headerPanel.add(labelsPanel, "cell 3 0,growx,aligny top");

		add(headerPanel, BorderLayout.NORTH);
		
				JPanel notificationPanel = new JPanel();
				notificationPanel.setBackground(null);
				headerPanel.add(notificationPanel, "cell 5 0,grow");

		// Menu Panel
		menuPanel = new JPanel();
		menuPanel.setBackground(null);
		menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		add(menuPanel, BorderLayout.CENTER);

		menuPanel.setLayout(new MigLayout("center", "[grow,fill][200][100][200][100][200,grow][grow,fill]",
				"[100px][][10px:30px:50px][grow][][10px:30px:50px]"));

		ImageIcon profileIcon = new ImageIcon(getClass().getResource("/images/96x96/profile.png"));

		ButtonPanel profileCBtn = new ButtonPanel(Color.decode("#002C4B"), profileIcon, "View/Update Profile",
				"    click to view or update profile information    ");
		profileCBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.frame.replaceContentPane("Profile", new ProfilePanel(employee, db), getLayout());
			}
		});
		menuPanel.add(profileCBtn, "cell 1 1,growx,aligny center");

		ImageIcon attendanceIcon = new ImageIcon(getClass().getResource("/images/96x96/attendance.png"));
		// " click to view or update profile information "
		ButtonPanel attendanceFormCBtn = new ButtonPanel(Color.decode("#002C4B"), attendanceIcon, "Fill Attendance",
				"       click to fill up attendance form      ");
		attendanceFormCBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.frame.replaceContentPane("Attendance Form", new AttendanceForm(employee, DB), getLayout());
			}
		});
		menuPanel.add(attendanceFormCBtn, "cell 3 1,growx,aligny center");

		ImageIcon printIcon = new ImageIcon(getClass().getResource("/images/96x96/invoice.png"));

		ButtonPanel checkPayrollCBtn = new ButtonPanel(Color.decode("#002C4B"), printIcon, "Check Payroll",
				"click to check Payroll");
		checkPayrollCBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.frame.replaceContentPane("Attendance Form", new CheckPayrollPanel(employee, DB), getLayout());
			}
		});
		menuPanel.add(checkPayrollCBtn, "cell 5 1,growx,aligny center");

		ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/images/logout.png"));

		JPanel printPayrollCBtn = new ButtonPanel(Color.decode("#002C4B"), printIcon, "Print Payslip",
				"click to print Payslip");
		printPayrollCBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
//
			}
		});
		menuPanel.add(printPayrollCBtn, "cell 1 3,growx,aligny center");

		JPanel logoutCBtn = new ButtonPanel(Color.decode("#002C4B"), logoutIcon, "Logout", null);
		logoutCBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.frame.replaceContentPane("Login", new LoginPanel(DB), new BorderLayout());

			}
		});
		menuPanel.add(logoutCBtn, "cell 3 3,grow");

	}

}