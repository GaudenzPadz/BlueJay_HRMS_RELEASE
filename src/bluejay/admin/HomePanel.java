package bluejay.admin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import com.formdev.flatlaf.FlatClientProperties;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class HomePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private EmployeeDatabase db;
    private ImageIcon listIcon = new ImageIcon(getClass().getResource("/images/list.png"));
    private ImageIcon recruitmentIcon = new ImageIcon(getClass().getResource("/images/recruitment.png"));
    private ImageIcon payrollIcon = new ImageIcon(getClass().getResource("/images/payroll.png")); // New icon for Payroll Overview
    JPanel mainPanel;
    private JLabel currentTimeLabel; // Label to display current time
    private Timer timer; // Timer for updating time label

    public HomePanel(EmployeeDatabase DB, JPanel mainPanel) {
        this.db = DB;
        this.mainPanel = mainPanel;
        JPanel dash = dashboard();
        add(dash);
        // Add the time panel
        JPanel timePanel = createTimePanel();
        add(timePanel);

        // Create and start the timer to update the time label every second
        timer = new Timer(1000, e -> updateTime());
        timer.start();
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

            // Calculate upcoming pay day (replace this with your actual logic to calculate upcoming pay day)
            String upcomingPayDay = calculateUpcomingPayDay(); // Assuming a method calculateUpcomingPayDay() is implemented

            // Create dashboard panels with dynamic data
            JPanel empOverview = dashboardPanel(listIcon, "Employee Overview", "Total Employees:",
                    String.valueOf(totalEmployees), "Departments:", String.valueOf(totalDepartments), "View More",
                    viewEmployeeListListener);

            JPanel attendanceOverview = dashboardPanel(recruitmentIcon, "Attendance Overview", "Total Check-Ins Today:",
                    String.valueOf(totalCheckInsToday), // Total check-ins
                    "New Hires Today:", String.valueOf(newHiresToday), // New hires today
                    "View Attendance", attendanceListener);

            // Create Payroll Overview panel
            JPanel payrollOverview = dashboardPanel(payrollIcon, "Payroll Overview", "Upcoming PayDay:", upcomingPayDay, // Replace "DD/MM/YYYY" with actual date
                    null, null, null, null); // Set null for the body and total labels, and action listener

            panel.add(empOverview, "");
            panel.add(attendanceOverview, "");
            panel.add(payrollOverview, ""); // Add the Payroll Overview panel
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching dashboard data: " + ex.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return panel;
    }

    private String calculateUpcomingPayDay() {
        // This method should calculate the upcoming pay day based on your business logic
        // For demonstration purposes, let's assume a fixed value here
        return "DD/MM/YYYY"; // Replace "DD/MM/YYYY" with the actual upcoming pay day
    }

    private JPanel dashboardPanel(ImageIcon icon, String title, String body1, String total1, String body2,
            String total2, String viewBtnText, ActionListener actionListener) {
        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 25 35 25 35", "[][][][118.00px,center][]",
                "[][20.00][][][pref!][][][]"));
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" + "[light]background:darken(@background,3%);" + "[dark]background:lighten(@background,3%)");
        JButton iconBtn = new JButton(
                new ImageIcon(icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
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

        if (actionListener != null) { // Add the button only if action listener is provided
            JButton viewPanelBtn = new JButton(viewBtnText);
            viewPanelBtn.addActionListener(actionListener);
            JLabel bodyLabel_2 = new JLabel(body2);
            panel.add(bodyLabel_2, "cell 3 5,alignx left");

            JLabel totalLabel_2 = new JLabel(total2);
            panel.add(totalLabel_2, "cell 4 5");
            panel.add(viewPanelBtn, "cell 3 7,grow");
        } else { // Add only labels if no action listener is provided
            if (body2 != null && total2 != null) {
                JLabel bodyLabel_2 = new JLabel(body2);
                panel.add(bodyLabel_2, "cell 3 5,alignx left");

                JLabel totalLabel_2 = new JLabel(total2);
                panel.add(totalLabel_2, "cell 4 5");
            }
        }

        return panel;
    }

    private JPanel createTimePanel() {
        JPanel timePanel = new JPanel(new MigLayout("wrap,fillx,insets 25 35 25 35", "[][][][][][][][]", "[]"));
        timePanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" + "[light]background:darken(@background,3%);" + "[dark]background:lighten(@background,3%)");

        // Adding current time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a"); // Updated pattern for 12-hour format with AM/PM
        LocalDateTime now = LocalDateTime.now();
        String currentTime = formatter.format(now);

        JLabel timeLabel = new JLabel("Current Time:");
        timePanel.add(timeLabel, "cell 0 0,alignx left");

        currentTimeLabel = new JLabel(currentTime); // Assign to instance variable
        timePanel.add(currentTimeLabel, "cell 1 0,alignx left");

        return timePanel;
    }

    // Method to update the time label
    private void updateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a"); // Updated pattern for 12-hour format with AM/PM
        LocalDateTime now = LocalDateTime.now();
        String currentTime = formatter.format(now);
        currentTimeLabel.setText(currentTime);
    }

    // Method to stop the timer when HomePanel is disposed
    public void dispose() {
        timer.stop();
    }
}