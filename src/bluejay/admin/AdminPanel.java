package bluejay.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;

import bluejay.ButtonPanel;
import bluejay.LoginPanel;
import bluejay.Main;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class AdminPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	public static JPanel mainPanel;
	public JPanel sidePanel;
	public ImageIcon backIcon = new ImageIcon(getClass().getResource("/images/back.png"));
	public ImageIcon homeIcon = new ImageIcon(getClass().getResource("/images/home.png"));
	public ImageIcon menuIcon = new ImageIcon(getClass().getResource("/images/menu.png"));
	public ImageIcon listIcon = new ImageIcon(getClass().getResource("/images/list.png"));
	public ImageIcon addEMPIcon = new ImageIcon(getClass().getResource("/images/add_emp.png"));
	public ImageIcon payrollIcon = new ImageIcon(getClass().getResource("/images/payroll.png"));
	public ImageIcon userIcon = new ImageIcon(getClass().getResource("/images/user.png"));
	public ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/images/logout.png"));
	private ImageIcon attendanceIcon = new ImageIcon(getClass().getResource("/images/attendance.png"));

	private EmployeeDatabase DB;

	public AdminPanel(EmployeeDatabase DB) {
		this.DB = DB;
		setLayout(new BorderLayout(5, 0));

		// Initialize panels
		mainPanel = new JPanel();

		mainPanel.setLayout(new BorderLayout(0, 0));
		mainPanel.add(new HomePanel(DB, mainPanel));

		sidePanel = SidePanel();
		add(sidePanel, BorderLayout.WEST);

		add(mainPanel, BorderLayout.CENTER);

	}

	private JPanel SidePanel() {
		// Initialize side panel
		Color bgBtnColor = Color.decode("#96E1CC");
		JPanel panel = new JPanel(new MigLayout("", "[left]", "[top][][][][][][][][][][]"));

		putClientProperty(FlatClientProperties.STYLE,
				"arc:20; [light]background:darken(@background,5%); [dark]background:lighten(@background,5%)");

		JPanel titlePanel = new JPanel();
		titlePanel.putClientProperty(FlatClientProperties.STYLE, "background:null");
		panel.add(titlePanel, "cell 0 0,growx,aligny top");
		JLabel iconLabel = new JLabel(userIcon);
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(2, 1));
		JLabel title = new JLabel("WELD WELL");
		title.setFont(new Font("Sans Serif", Font.BOLD, 20));
		JLabel label2 = new JLabel("HRMS for a Welding Shop");
		textPanel.add(title);
		textPanel.add(label2);
		titlePanel.add(iconLabel);
		titlePanel.add(textPanel);

		JPanel homeBtn = new ButtonPanel(bgBtnColor, "Home", homeIcon);
		panel.add(homeBtn, "cell 0 2,growx,aligny center");
		homeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Show HomePanel
				mainPanel.removeAll();
				mainPanel.setLayout(new BorderLayout());
				mainPanel.add(new HomePanel(DB, mainPanel));
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});

		JPanel employeeListBtn = new ButtonPanel(bgBtnColor, "Employee List", listIcon);
		panel.add(employeeListBtn, "cell 0 3,growx,aligny center");
		employeeListBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Show Employee List Panel
				mainPanel.removeAll();
				mainPanel.setLayout(new BorderLayout());
				mainPanel.add(new EMPListPanel(DB), BorderLayout.CENTER);
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});

		JPanel addEMPBtn = new ButtonPanel(bgBtnColor, "Add Employee", addEMPIcon);
		panel.add(addEMPBtn, "cell 0 4,growx,aligny center");
		addEMPBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Shows Add Employee Panel
				mainPanel.removeAll();
				mainPanel.setLayout(new MigLayout("", "[grow]", "[grow][grow,center]"));
				JLabel titleLabel = new JLabel("Add Employee");
				titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
				mainPanel.add(titleLabel, "cell 0 0,alignx leading,aligny center");
				mainPanel.add(new AddEMPPanel(DB), "cell 0 1,grow");
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});

		JPanel attendanceBtn = new ButtonPanel(bgBtnColor, "Attendance Monitor", attendanceIcon);
		panel.add(attendanceBtn, "cell 0 6,growx,aligny center");
		attendanceBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainPanel.removeAll();
				mainPanel.setLayout(new BorderLayout());
				mainPanel.add(new AttendancePanel(DB), BorderLayout.CENTER);
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});

		JPanel payrollBtn = new ButtonPanel(bgBtnColor, "Payroll", payrollIcon);
		panel.add(payrollBtn, "cell 0 5,growx,aligny center");
		payrollBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainPanel.removeAll();
				mainPanel.setLayout(new BorderLayout());
				mainPanel.add(new PayrollPanel(DB), BorderLayout.CENTER);
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});

		JPanel logoutBtn = new ButtonPanel(bgBtnColor, "Logout", logoutIcon);
		panel.add(logoutBtn, "cell 0 10,growx,aligny center");
		logoutBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.frame.replaceContentPane("Login", new LoginPanel(DB), new BorderLayout());
			}
		});

		titlePanel.putClientProperty(FlatClientProperties.STYLE,
				"arc:20; [light]background:darken(@background,5%); [dark]background:lighten(@background,5%)");

		return panel;
	}
}
