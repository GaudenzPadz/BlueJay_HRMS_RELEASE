package bluejay.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LayoutManager;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;

import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.pie.PieChart;

public class HomePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private EmployeeDatabase db;
	private ImageIcon listIcon = new ImageIcon(getClass().getResource("/images/list.png"));
	private ImageIcon recruitmentIcon = new ImageIcon(getClass().getResource("/images/recruitment.png"));
	private ImageIcon payrollIcon = new ImageIcon(getClass().getResource("/images/payroll.png")); // New icon for
																									// Payroll
																									// Overview
	private JLabel currentTimeLabel; // Label to display current time
	private JPanel pie;
	private PieChart employeesPie;
	private PieChart pieChart2;
	private JLabel currentDataLabel;
	private JLabel newHiresTodayNumLabel;
	private JLabel absentsNumLabel;
	private JLabel presentsNumLabel;
	private JLabel totalEMPNumLabel;

	private void initializeComponents() {
		currentTimeLabel = new JLabel("time label");
		currentDataLabel = new JLabel("current date");
		newHiresTodayNumLabel = new JLabel("0");
		absentsNumLabel = new JLabel("0");
		presentsNumLabel = new JLabel("0");
		totalEMPNumLabel = new JLabel("0");

	}

	public HomePanel(EmployeeDatabase DB) {
		this.db = DB;
		setLayout(new MigLayout("", "[grow]", "[grow]"));
		initializeComponents();

		JPanel panel = new JPanel();
		add(panel, "cell 0 0,grow");
		panel.setLayout(new BorderLayout(0, 0));

		JPanel body = new JPanel(new MigLayout("", "[:200px:260px,grow][::457.00px,grow 200,fill][grow]", "[][57px][4px][57px][4px][57px][4px][57px][4px][249px][10px]"));
		panel.add(body, BorderLayout.CENTER);

		JPanel presentPanel = new JPanel();
		presentPanel.putClientProperty(FlatClientProperties.STYLE,
				"arc:20;[light]background:darken(@background,3%);[dark]background:lighten(@background,3%);background:#BBE1FA");

		JPanel titlePanel = new JPanel(new MigLayout("", "[46px][grow][grow 10]", "[14px]"));
		body.add(titlePanel, "cell 0 0 2 1,grow");

		JLabel lblNewLabel = new JLabel("Welcome, Admin!");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		titlePanel.add(lblNewLabel, "cell 0 0,alignx left,aligny top");

		titlePanel.add(currentDataLabel, "cell 1 0,alignx right");

		titlePanel.add(currentTimeLabel, "cell 2 0,alignx right,growy");

		body.add(presentPanel, "cell 0 1,grow");
		presentPanel.setLayout(new MigLayout("", "[grow]", "[grow][14px,grow]"));

		JLabel lblNewLabel_5 = new JLabel("Present");
		presentPanel.add(lblNewLabel_5, "cell 0 0");

		presentsNumLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		presentPanel.add(presentsNumLabel, "cell 0 1");
		pie = new JPanel();
		pie.setLayout(new MigLayout("", "[400px,grow]", "[300px,grow][300px,grow]"));
		employeesPie = new PieChart();
		pie.add(employeesPie, "cell 0 0,growx,aligny top");

		body.add(pie, "cell 1 1 1 9,grow");
		pieChart2 = new PieChart();
		JLabel header2 = new JLabel("PIE CHART");
		header2.putClientProperty(FlatClientProperties.STYLE, "font:+1");
		pieChart2.setHeader(header2);
		pieChart2.getChartColor().addColor(Color.decode("#f87171"), Color.decode("#fb923c"), Color.decode("#fbbf24"),
				Color.decode("#a3e635"));
		pieChart2.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
		dataset.addValue("Inantok", 40);
		dataset.addValue("Sleepy", 60);
		pieChart2.setDataset(dataset);
		// pie.add(pieChart2, "cell 0 1,growx,aligny top");

		JPanel absentPanel = new JPanel();
		absentPanel.putClientProperty(FlatClientProperties.STYLE,
				"arc:20;[light]background:darken(@background,3%);[dark]background:lighten(@background,3%);background:#70E4EF");
		body.add(absentPanel, "cell 0 3,grow");
		absentPanel.setLayout(new MigLayout("", "[grow]", "[14px,grow][grow][grow]"));

		JLabel lblNewLabel_4_1 = new JLabel("Absent");
		absentPanel.add(lblNewLabel_4_1, "cell 0 0");

		absentsNumLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		absentPanel.add(absentsNumLabel, "cell 0 1");

		JPanel newHiresPanel = new JPanel();
		newHiresPanel.putClientProperty(FlatClientProperties.STYLE,
				"arc:20;[light]background:darken(@background,3%);[dark]background:lighten(@background,3%);background:#EB9FEF");
		body.add(newHiresPanel, "cell 0 5,grow");
		newHiresPanel.setLayout(new MigLayout("", "[grow]", "[][]"));

		JLabel lblNewLabel_3 = new JLabel("New Hires Today");
		newHiresPanel.add(lblNewLabel_3, "cell 0 0");

		newHiresTodayNumLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		newHiresPanel.add(newHiresTodayNumLabel, "cell 0 1");

		JPanel totalEMPPanel = new JPanel((LayoutManager) null);
		body.add(totalEMPPanel, "cell 0 7,grow");
		totalEMPPanel.setLayout(new MigLayout("", "[grow]", "[][][]"));

		JLabel lblNewLabel_3_1 = new JLabel("Total Employees");
		totalEMPPanel.add(lblNewLabel_3_1, "cell 0 0,growx");

		totalEMPNumLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		totalEMPPanel.add(totalEMPNumLabel, "cell 0 1,growx");

		JPanel notificationPanel = new JPanel();
		body.add(notificationPanel, "cell 0 9,grow");

		JPanel panel_5 = new JPanel();
		body.add(panel_5, "cell 0 10,grow");
		refreshData();

		updateCurrentTime();
		updateCurrentDate();
		updatePresentEmployees();
		updateAbsentEmployees();
		updateNewHiresToday();

	}

	private void refreshData() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Remove all components
				pie.removeAll();
				pie.setLayout(new MigLayout("", "[400px,grow]", "[300px,grow][300px,grow]"));

				// Reinitialize the pie chart
				employeesPie = new PieChart();
				pie.add(employeesPie, "cell 0 0,growx,aligny top");

				JLabel header1 = new JLabel("Employee Overview");
				header1.putClientProperty(FlatClientProperties.STYLE, "font:+1");
				employeesPie.setHeader(header1);
				employeesPie.getChartColor().addColor(Color.decode("#f87171"), Color.decode("#fb923c"),
						Color.decode("#fbbf24"), Color.decode("#a3e635"));
				employeesPie.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");
				employeesPie.setDataset(employeesData());

				// Repaint and revalidate the panel
				pie.repaint();
				pie.revalidate();
				employeesPie.startAnimation();
			}
		});
	}

	private void updateCurrentTime() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
		currentTimeLabel.setText(timeFormat.format(new Date()));
	}

	private void updateCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		currentDataLabel.setText(dateFormat.format(new Date()));
	}

	private int totalEmployees = 0;

	private DefaultPieDataset employeesData() {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

		// Populate dataset with employee work types and calculate total employees
		int shieldedMetalArcWelding = db.getEmployeeCountByType("Shielded Metal Arc Welding");
		dataset.addValue("Shielded Metal Arc Welding", shieldedMetalArcWelding);

		int metalInertGasWelding = db.getEmployeeCountByType("Metal Inert Gas Welding");
		dataset.addValue("Metal Inert Gas Welding", metalInertGasWelding);

		int fluxCoredArcWelding = db.getEmployeeCountByType("Flux-cored Arc Welding");
		dataset.addValue("Flux-cored Arc Welding", fluxCoredArcWelding);

		int gasTungstenArcWelding = db.getEmployeeCountByType("Gas Tungsten Arc Welding");
		dataset.addValue("Gas Tungsten Arc Welding", gasTungstenArcWelding);

		totalEmployees += gasTungstenArcWelding + shieldedMetalArcWelding + metalInertGasWelding + fluxCoredArcWelding;

		// Update the total employee count label
		System.out.println("NUM OF TOTAL EMP : " + String.valueOf(totalEmployees));
		totalEMPNumLabel.setText(String.valueOf(totalEmployees));

		return dataset;
	}

	private void updatePresentEmployees() {
		int presentCount = db.getPresentEmployeeCount();
		System.out.println(presentCount);
		presentsNumLabel.setText(String.valueOf(presentCount));
	}

	private void updateAbsentEmployees() {
		int absentCount = db.getAbsentEmployeeCount();
		System.out.println(absentCount);
		absentsNumLabel.setText(String.valueOf(absentCount));
	}

	private void updateNewHiresToday() {
		int newHiresCount = db.getNewHiresTodayCount();
		System.out.println(newHiresCount);
		newHiresTodayNumLabel.setText(String.valueOf(newHiresCount));
	}

}
