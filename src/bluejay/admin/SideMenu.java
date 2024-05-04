package bluejay.admin;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SideMenu {
	private JPanel sidePanel;
	private JPanel mainPanel;
	private JButton toggleButton;
	private boolean isOpen;
	private int sideWidth = 100; // default value when side is closed
	private int sideMaxWidth = 300; // default value when side is opened
	private int animationDuration = 100; // Animation duration in milliseconds
	private Timer timer;

	public SideMenu(JPanel sidePanel, JPanel mainPanel, boolean isOpen) {
		this.sidePanel = sidePanel;
		this.mainPanel = mainPanel;
		this.isOpen = isOpen;
	}

	public void setSideMaxWidth(int sideMaxWidth) {
		this.sideMaxWidth = sideMaxWidth;
	}

	public int getSideMaxWidth() {
		return sideMaxWidth;
	}

	public void setSideMinimumWidth(int sideWidth) {
		this.sideWidth = sideWidth;
	}

	public int getSideMinimumWidth() {
		return sideWidth;
	}

	public void setAnimationDuration(int animationDuration) {
		this.animationDuration = animationDuration;
	}

	public int getAnimationDuration() {
		return animationDuration;
	}

	public JButton getToggleButton() {
		return toggleButton;
	}

	public void setOpened(boolean isOpened) {
		isOpened = isOpen;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void toggleMenu() {
		if (isOpen) {
			// Close the side menu
			sidePanel.setPreferredSize(new Dimension(getSideMinimumWidth(), mainPanel.getHeight()));
			mainPanel.revalidate();
			isOpen = false;
		} else {
			// Open the side menu
			sidePanel.setPreferredSize(new Dimension(getSideMaxWidth(), mainPanel.getHeight()));
			mainPanel.revalidate();
			isOpen = true;
		}
	}

	public void toggleMenuWithAnimation() {
		// Calculate the target width based on the menu state

		// If the menu is open, target width is minimum width; otherwise, it's maximum
		// width
		int targetWidth = isOpen ? getSideMinimumWidth() : getSideMaxWidth();

		// Get the current width of the side panel
		int initialWidth = sidePanel.getWidth();

		// Calculate the step size for the animation
		// Calculate the step size based on animation duration; dividing by 10 for
		// smoother animation
		int stepSize = (targetWidth - initialWidth) / (getAnimationDuration() / 10);

		// Create a timer for animation
		timer = new Timer(10, new ActionListener() { // Timer triggers every 10 milliseconds
			public void actionPerformed(ActionEvent e) {
				int newWidth = sidePanel.getWidth() + stepSize; // Calculate the new width based on the step size
				if ((stepSize > 0 && newWidth >= targetWidth) || (stepSize < 0 && newWidth <= targetWidth)) {
					// If the new width is greater than or equal to the target width (for
					// expanding), or less than or equal to the target width (for collapsing)
					timer.stop(); // Stop the timer
					isOpen = !isOpen; // Toggle the menu state
					return;
				}
				sidePanel.setPreferredSize(new Dimension(newWidth, sidePanel.getHeight())); // Set the new preferred
																							// size for the side panel
				sidePanel.revalidate(); // Revalidate the side panel to update its layout
				mainPanel.revalidate(); // Revalidate the main panel to update its layout
			}
		});

		// Start the animation timer
		timer.start();
	}

}
