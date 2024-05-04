package bluejay;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

public class GUI extends JFrame {
	private ImageIcon logo;
	private ImageIcon scaledLogo;

	public GUI(String title, JComponent contentPanel, int width, int height, boolean resize, boolean visible) {
		super(title); // Use super constructor for JFrame initialization

		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(resize);
		setLocationRelativeTo(null); // Center on screen
		setContentPane(contentPanel);
		setVisible(visible);
		setMinimumSize(new Dimension(width - 300, height - 200));

		// Load logo using getResource (assuming logo.png is in the same package)
		logo = new ImageIcon(getClass().getResource("/images/logo.png"));
		setIconImage(logo.getImage()); // Set logo for the frame
	}

	public void setScaledLogo(int width, int height) {
		scaledLogo = new ImageIcon(logo.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	public ImageIcon getOriginalLogo() {
		return logo; // Return original logo (unchanged)
	}

	public ImageIcon getScaledLogo() {
		return scaledLogo;
	}

	// a method to be called and sets if light mode (FlatDarculaLaf) or dark mode
	// (FlatLightLaf)
	public void isDark(boolean darkMode) {
		try {
			if (darkMode) {
				FlatAnimatedLafChange.showSnapshot();
				FlatDarculaLaf.setup();

			} else {
				FlatAnimatedLafChange.showSnapshot();
				FlatIntelliJLaf.setup();

			}
			UIManager.put("Button.arc", 800);
			UIManager.put("Component.arc", 50);
			UIManager.put("ProgressBar.arc", 999);
			UIManager.put("TextComponent.arc", 50);		
			UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 13));


			SwingUtilities.updateComponentTreeUI(this);
			FlatAnimatedLafChange.hideSnapshotWithAnimation();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// a method to use to replace the panels
	public void replaceContentPane(String title, JPanel form, LayoutManager manager) {
		EventQueue.invokeLater(() -> {
//			FlatAnimatedLafChange.showSnapshot();
			getContentPane().removeAll();
			getContentPane().revalidate();
			getContentPane().repaint();
			getContentPane().setLayout(manager);
			this.setTitle(title);
			setContentPane(form);
//			FlatAnimatedLafChange.hideSnapshotWithAnimation();
		});
	}

}