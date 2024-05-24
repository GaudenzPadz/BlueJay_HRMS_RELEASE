package bluejay;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import bluejayDB.EmployeeDatabase;
import bluejay.Components.GUI;
import bluejay.Components.SplashScreen;
import bluejay.Components.LoginPanel;
// Main application logic with splash screen and progress update
public class Main {

	public static GUI frame;
	public static EmployeeDatabase DB;
	public static Employee emp;

	public static void main(String[] args) {

		SplashScreen splashScreen = new SplashScreen();
		splashScreen.setVisible(true);

		// Simulate background work
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(() -> {
			try {
				splashScreen.setStatus("Connecting to Database...");
				splashScreen.updateProgress(20); // Simulate progress

				Thread.sleep(2000); // Simulate delay

				try {
					DB = new EmployeeDatabase();

				} catch (ClassNotFoundException | SQLException e) {

					JOptionPane.showMessageDialog(null, "Failed to connect to the database." + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();

				}
				splashScreen.setStatus("Setting up GUI...");
				splashScreen.updateProgress(60); // Simulate progress
			
				frame = new GUI("Login", new LoginPanel(DB), 1200, 700, true, false);
				frame.isDark(false);

				Thread.sleep(2000); // Simulate delay
				splashScreen.updateProgress(100); // Complete progress

				SwingUtilities.invokeLater(() -> {
					splashScreen.setVisible(false);
					frame.setVisible(true);

				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				executorService.shutdown();
			}
		});

	}
}
