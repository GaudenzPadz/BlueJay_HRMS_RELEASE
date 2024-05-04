package bluejay.admin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatClientProperties;

import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class HomePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private EmployeeDatabase db;
	private ImageIcon listIcon = new ImageIcon(getClass().getResource("/images/list.png"));
	private ImageIcon recruitmentIcon = new ImageIcon(getClass().getResource("/images/recruitment.png"));
	JPanel mainPanel;
	
	public HomePanel(EmployeeDatabase DB, JPanel mainPanel) {
		this.db = DB;
		this.mainPanel = mainPanel;
		JPanel dash = dashboard();
		add(dash);
	}

	private JPanel dashboard() {
		// Create a main panel with MigLayout for flexibility
		JPanel panel = new JPanel(new MigLayout("", "[][]", "[][][]"));

		// Create action listeners for the dashboard buttons
		ActionListener viewEmployeeListListener = e -> {
			mainPanel.removeAll();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(new EMPListPanel(db), BorderLayout.CENTER);
			mainPanel.revalidate();
			mainPanel.repaint();
		};

		ActionListener attendanceListener = e -> {
			mainPanel.removeAll();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(new AttendancePanel(db), BorderLayout.CENTER);
			mainPanel.revalidate();
			mainPanel.repaint();
		};

		try {
			// Fetch data from the database for the dashboard overview
			int totalEmployees = db.getAllEmployees().size();
			int totalDepartments = db.getDepartments().getFetchSize();
			int totalCheckInsToday = db.countCheckInsToday(); // Method to count check-ins for today
			int newHiresToday = db.countNewEmployeesToday(); // Method to count new hires today

			// Create dashboard panels with dynamic data
			JPanel empOverview = dashboardPanel(listIcon, "Employee Overview", "Total Employees:",
					String.valueOf(totalEmployees), "Departments:", String.valueOf(totalDepartments), "View More",
					viewEmployeeListListener);

			JPanel attendanceOverview = dashboardPanel(recruitmentIcon, "Attendance Overview", "Total Check-Ins Today:",
					String.valueOf(totalCheckInsToday), // Total check-ins
					"New Hires Today:", String.valueOf(newHiresToday), // New hires today
					"View Attendance", attendanceListener);

			panel.add(empOverview, "");
			panel.add(attendanceOverview, "");
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Error fetching dashboard data: " + ex.getMessage(), "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return panel;
	}

	private JPanel dashboardPanel(ImageIcon icon, String title, String body1, String total1, String body2,
			String total2, String viewBtnText, ActionListener actionListener) {
		JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 25 35 25 35", "[][][][118.00px,center][]",
				"[][20.00][][][pref!][][][]"));
		panel.putClientProperty(FlatClientProperties.STYLE,
				"arc:20;" + "[light]background:darken(@background,3%);" + "[dark]background:lighten(@background,3%)");
		JButton iconBtn = new JButton(new ImageIcon(icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
		iconBtn.setHorizontalAlignment(SwingConstants.LEADING);
		iconBtn.setOpaque(false);
		iconBtn.setContentAreaFilled(false);
		iconBtn.setBorderPainted(false);

		panel.add(iconBtn, "cell 0 0 4 1,alignx left,aligny center");

		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

		panel.add(titleLabel, "cell 3 1,alignx left");

		JLabel bodyLabel_1 = new JLabel(body1);
		panel.add(bodyLabel_1, "cell 3 3,alignx left");

		JLabel totalLabel_1 = new JLabel(total1);
		panel.add(totalLabel_1, "cell 4 3,alignx left");

		JButton viewPanelBtn = new JButton(viewBtnText);
		viewPanelBtn.addActionListener(actionListener);

		JLabel bodyLabel_2 = new JLabel(body2);
		panel.add(bodyLabel_2, "cell 3 5,alignx left");

		JLabel totalLabel_2 = new JLabel(total2);
		panel.add(totalLabel_2, "cell 4 5");
		panel.add(viewPanelBtn, "cell 3 7,grow");

		return panel;
	}
}
